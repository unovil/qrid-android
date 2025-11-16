package com.unovil.tardyscan.core.data.repository

import com.unovil.tardyscan.core.data.dto.AttendanceDto
import com.unovil.tardyscan.core.data.dto.StudentDto
import com.unovil.tardyscan.core.model.Attendance
import io.github.jan.supabase.storage.DownloadStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface AttendanceRepository {
    suspend fun createAttendance(attendance: Attendance)
    suspend fun getAttendances(date: LocalDate): List<AttendanceDto>
    suspend fun getAllStudentInfos(): List<StudentDto>
    suspend fun getStudentInfo(id: Long): StudentDto?
    suspend fun getAvatarFlow(avatarLink: String): Flow<DownloadStatus>
    suspend fun getDecryptionKey(schoolId: Int): String?
    suspend fun deleteAttendance(id: Int)
}
