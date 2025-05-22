package com.unovil.tardyscan.data.repository.impl

import android.util.Log
import com.unovil.tardyscan.data.network.dto.AttendanceDto
import com.unovil.tardyscan.data.network.dto.StudentDto
import com.unovil.tardyscan.data.repository.AttendanceRepository
import com.unovil.tardyscan.domain.model.Attendance
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class AttendanceRepositoryImpl @Inject constructor(
    postgrest: Postgrest
) : AttendanceRepository {

    private val attendanceTable = postgrest["attendances"]
    private val studentTable = postgrest["students"]

    override suspend fun createAttendance(attendance: Attendance): Boolean {
        return try {
            val attendanceDto = AttendanceDto(
                studentId = attendance.studentId,
                timestamp = attendance.timestamp,
            )
            attendanceTable.insert(attendanceDto)

            true
        } catch (e: Exception) {
            throw e
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
}