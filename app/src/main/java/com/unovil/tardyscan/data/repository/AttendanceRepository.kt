package com.unovil.tardyscan.data.repository

import com.unovil.tardyscan.data.network.dto.AttendanceDto
import com.unovil.tardyscan.data.network.dto.StudentDto
import com.unovil.tardyscan.domain.model.Attendance

interface AttendanceRepository {
    suspend fun createAttendance(attendance: Attendance): Boolean
    suspend fun getAttendances(): List<AttendanceDto>?
    suspend fun getAttendance(id: Int): AttendanceDto
    suspend fun deleteAttendance(id: Int)
    suspend fun getStudentInfo(id: Long): StudentDto?
    suspend fun getDecryptionKey(schoolId: Int): String?
}