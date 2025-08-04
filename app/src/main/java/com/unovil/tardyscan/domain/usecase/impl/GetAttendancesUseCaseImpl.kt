package com.unovil.tardyscan.domain.usecase.impl

import android.util.Log
import com.unovil.tardyscan.data.repository.AttendanceRepository
import com.unovil.tardyscan.domain.model.Attendance
import com.unovil.tardyscan.domain.usecase.GetAttendancesUseCase
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import javax.inject.Inject

class GetAttendancesUseCaseImpl @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) : GetAttendancesUseCase {
    override suspend fun execute(input: GetAttendancesUseCase.Input): GetAttendancesUseCase.Output =
        withContext(Dispatchers.IO) {
            try {
                val attendanceList = mutableListOf<Attendance>()
                Log.d("GetAttendancesUseCaseImpl", "execute with: ${input.date}")

                val presentList = attendanceRepository.getAttendances(input.date)
                val presentListIds = presentList.map { it.studentId }.toSet() // Use Set for fast lookup

                attendanceRepository.getAllStudentInfos().forEach { student ->
                    val isPresent = student.id in presentListIds
                    val timestamp = presentList.find { it.studentId == student.id }?.timestamp ?: Instant.DISTANT_PAST

                    attendanceList.add(
                        Attendance(
                            studentId = student.id!!,
                            timestamp = timestamp,
                            name = "${student.lastName}, ${student.firstName} ${student.middleName.orEmpty()}",
                            level = student.section.level,
                            section = student.section.section,
                            isPresent = isPresent
                        )
                    )
                }

                GetAttendancesUseCase.Output.Success(attendanceList)
            } catch (e: Exception) {
                Log.e("GetAttendancesUseCaseImpl", "Error fetching attendances: ${e.message}")
                when (e) {
                    is PostgrestRestException -> GetAttendancesUseCase.Output.Failure.PostgrestException
                    is HttpRequestException -> GetAttendancesUseCase.Output.Failure.HttpRequestException
                    is HttpRequestTimeoutException -> GetAttendancesUseCase.Output.Failure.HttpRequestTimeout
                    else -> GetAttendancesUseCase.Output.Failure.Unknown(e)
                }
            }
        }
}