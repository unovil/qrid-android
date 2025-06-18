package com.unovil.tardyscan.data.repository.impl

import android.util.Log
import com.unovil.tardyscan.data.network.dto.AttendanceDto
import com.unovil.tardyscan.data.network.dto.SchoolDto
import com.unovil.tardyscan.data.network.dto.StudentDto
import com.unovil.tardyscan.data.repository.AttendanceRepository
import com.unovil.tardyscan.data.repository.AttendanceRepository.CreateAttendanceResult
import com.unovil.tardyscan.domain.model.Attendance
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import kotlin.time.Duration

class AttendanceRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth
) : AttendanceRepository {

    private val attendanceTable = postgrest["attendances"]
    private val studentTable = postgrest["students"]

    override suspend fun createAttendance(attendance: Attendance): CreateAttendanceResult {
        val startOfDay = attendance.timestamp
            .toLocalDateTime(TimeZone.UTC).date
            .atStartOfDayIn(TimeZone.UTC)
            
        val endOfDay = startOfDay
            .plus(Duration.parse("24h"))
            .minus(Duration.parse("1ms"))

        try {
            val existingAttendance = attendanceTable.select {
                filter {
                    and {
                        AttendanceDto::studentId eq attendance.studentId
                        AttendanceDto::timestamp gte startOfDay
                        AttendanceDto::timestamp lte endOfDay
                    }
                }
            }.decodeList<AttendanceDto>()

            if (existingAttendance.isNotEmpty()) {
                return CreateAttendanceResult.Failure.AttendanceExists
            }

            val attendanceDto = AttendanceDto(
                studentId = attendance.studentId,
                timestamp = attendance.timestamp,
            )
            attendanceTable.insert(attendanceDto)

            return CreateAttendanceResult.Success
        } catch (e: Exception) {
            e.printStackTrace()
            return CreateAttendanceResult.Failure.UnknownError(e)
        }
    }

    override suspend fun getAttendances(): List<AttendanceDto>? {
        return attendanceTable.select().decodeList<AttendanceDto>()
    }

    override suspend fun getAttendance(id: Int): AttendanceDto {
        return attendanceTable.select {
            filter {
                AttendanceDto::id eq id
            }
        }.decodeSingle<AttendanceDto>()
    }

    override suspend fun deleteAttendance(id: Int) {
        attendanceTable.delete {
            filter {
                AttendanceDto::id eq id
            }
        }
    }

    override suspend fun getStudentInfo(id: Long): StudentDto? {
        Log.d("AttendanceRepository", "getStudentInfo called with id: $id")

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
        ) {
            filter { StudentDto::id eq id }
        }.decodeSingleOrNull<StudentDto>()
    }

    override suspend fun getDecryptionKey(schoolId: Int): String? {
        Log.d("AttendanceRepository", "getDecryptionKey called with user ID: ${auth.currentSessionOrNull()?.user?.id}")
        val result = postgrest["schools"].select(Columns.list("decryption_key")) {
            filter { SchoolDto::id eq schoolId }
        }.decodeSingleOrNull<Map<String, String>>()
        return result?.get("decryption_key")
    }
}
