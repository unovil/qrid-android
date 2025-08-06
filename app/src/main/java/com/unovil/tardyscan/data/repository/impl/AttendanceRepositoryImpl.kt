package com.unovil.tardyscan.data.repository.impl

import android.util.Log
import com.unovil.tardyscan.data.network.dto.AttendanceDto
import com.unovil.tardyscan.data.network.dto.SchoolDto
import com.unovil.tardyscan.data.network.dto.StudentDto
import com.unovil.tardyscan.data.repository.AttendanceRepository
import com.unovil.tardyscan.domain.model.Attendance
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.DownloadStatus
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.downloadAuthenticatedAsFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.JsonPrimitive
import javax.inject.Inject
import kotlin.time.Duration

class AttendanceRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth,
    private val storage: Storage
) : AttendanceRepository {

    private val attendanceTable = postgrest["attendances"]
    private val studentTable = postgrest["students"]
    private val avatarsBucket = storage["avatars"]

    override suspend fun createAttendance(attendance: Attendance) {
        val startOfDay = attendance.timestamp
            .toLocalDateTime(TimeZone.UTC).date
            .atStartOfDayIn(TimeZone.UTC)

        Log.d("AttendanceRepositoryImpl", "startOfDay: $startOfDay")

        val endOfDay = startOfDay
            .plus(Duration.parse("24h"))
            .minus(Duration.parse("1ms"))

        Log.d("AttendanceRepositoryImpl", "endOfDay: $endOfDay")

        val existingAttendance = attendanceTable.select {
            filter {
                and {
                    AttendanceDto::studentId eq attendance.studentId
                    AttendanceDto::timestamp gte startOfDay
                    AttendanceDto::timestamp lte endOfDay
                }
            }
        }.decodeList<AttendanceDto>()

        Log.d("AttendanceRepositoryImpl", "existingAttendance: $existingAttendance")

        if (existingAttendance.isNotEmpty()) {
            throw IllegalAccessException("Attendance already exists")
        }

        val userId = (auth.currentUserOrNull()!!.userMetadata!!["allowed_user_id"] as JsonPrimitive).content.toInt()

        val attendanceDto = AttendanceDto(
            studentId = attendance.studentId,
            timestamp = attendance.timestamp,
            senderId = userId
        )

        attendanceTable.insert(attendanceDto)
    }

    override suspend fun getAttendances(date: LocalDate): List<AttendanceDto> {
        // return attendanceDao.getAttendances()
        Log.d("AttendanceRepositoryImpl", "localdate: $date")

        val startOfDay = date.atStartOfDayIn(TimeZone.currentSystemDefault())

        val endOfDay = startOfDay
            .plus(Duration.parse("24h"))
            .minus(Duration.parse("1ms"))

        val attendanceList = attendanceTable.select {
            filter {
                and {
                    AttendanceDto::timestamp gte startOfDay
                    AttendanceDto::timestamp lte endOfDay
                }
            }
        }.decodeList<AttendanceDto>()

        return attendanceList
    }

    override suspend fun deleteAttendance(id: Int) {
        attendanceTable.delete {
            filter {
                AttendanceDto::id eq id
            }
        }
    }

    // no need for avatar, this one is used for history
    override suspend fun getAllStudentInfos(): List<StudentDto> {
        return studentTable.select(columns =
            Columns.raw("""
                id, last_name, first_name, middle_name,
                sections (
                    level, section,
                    schools (
                        name, domain
                    )
                )
            """.trimIndent())
        ).decodeList<StudentDto>()
    }

    override suspend fun getStudentInfo(id: Long): StudentDto? {
        Log.d("AttendanceRepository", "getStudentInfo called with id: $id")

        return studentTable.select(columns =
            Columns.raw("""
                id, last_name, first_name, middle_name, avatar,
                sections (
                    level, section,
                    schools (
                        name, domain
                    )
                )
            """.trimIndent())
        ) {
            filter { StudentDto::id eq id }
        }.decodeSingleOrNull<StudentDto>()
    }

    override suspend fun getAvatarFlow(avatarLink: String): Flow<DownloadStatus> {
        return avatarsBucket.downloadAuthenticatedAsFlow(avatarLink)
    }

    override suspend fun getDecryptionKey(schoolId: Int): String? {
        Log.d("AttendanceRepository", "getDecryptionKey called with user ID: ${auth.currentSessionOrNull()?.user?.id}")
        val result = postgrest["schools"].select(Columns.list("decryption_key")) {
            filter { SchoolDto::id eq schoolId }
        }.decodeSingleOrNull<Map<String, String>>()
        return result?.get("decryption_key")
    }
}
