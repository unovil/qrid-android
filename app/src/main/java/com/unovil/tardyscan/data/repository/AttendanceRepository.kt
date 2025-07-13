package com.unovil.tardyscan.data.repository

import com.unovil.tardyscan.data.network.dto.AttendanceDto
import com.unovil.tardyscan.data.network.dto.StudentDto
import com.unovil.tardyscan.domain.model.Attendance
import kotlinx.datetime.LocalDate

interface AttendanceRepository {
    sealed class CreateAttendanceResult {
        object Success : CreateAttendanceResult()

        sealed class Failure : CreateAttendanceResult() {
            object AttendanceExists : Failure()
            data class UnknownError(val e: Exception) : Failure()
        }
    }

    suspend fun createAttendance(attendance: Attendance): CreateAttendanceResult
    suspend fun getAttendances(date: LocalDate): List<AttendanceDto>
    suspend fun getAllStudentIds(): List<Long>
    suspend fun deleteAttendance(id: Int)
    suspend fun getStudentInfo(id: Long): StudentDto?
    suspend fun getDecryptionKey(schoolId: Int): String?
}