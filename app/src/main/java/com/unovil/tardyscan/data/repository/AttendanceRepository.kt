package com.unovil.tardyscan.data.repository

import com.unovil.tardyscan.data.local.entity.AttendanceEntity
import com.unovil.tardyscan.data.network.dto.AttendanceDto
import com.unovil.tardyscan.data.network.dto.StudentDto
import com.unovil.tardyscan.domain.model.Attendance
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    sealed class CreateAttendanceResult {
        object Success : CreateAttendanceResult()

        sealed class Failure : CreateAttendanceResult() {
            object AttendanceExists : Failure()
            data class UnknownError(val e: Exception) : Failure()
        }
    }

    suspend fun createAttendance(attendance: Attendance): CreateAttendanceResult
    fun getAttendances(): Flow<List<AttendanceEntity>>
    suspend fun getAttendance(id: Int): AttendanceDto
    suspend fun deleteAttendance(id: Int)
    suspend fun getStudentInfo(id: Long): StudentDto?
    suspend fun getDecryptionKey(schoolId: Int): String?
}