package com.unovil.tardyscan.data.repository.impl

import com.unovil.tardyscan.data.network.dto.AttendanceDto
import com.unovil.tardyscan.data.repository.AttendanceRepository
import com.unovil.tardyscan.domain.model.Attendance
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Inject

class AttendanceRepositoryImpl @Inject constructor(
    postgrest: Postgrest
) : AttendanceRepository {

    private val attendanceTable = postgrest["attendances"]

    override suspend fun createAttendance(attendance: Attendance): Boolean {
        return try {
            val attendanceDto = AttendanceDto(
                studentId = attendance.studentId,
                date = attendance.date,
                isPresent = attendance.isPresent
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
}