package com.unovil.tardyscan.domain.usecase.impl

import com.unovil.tardyscan.data.repository.AttendanceRepository
import com.unovil.tardyscan.domain.usecase.CreateAttendanceUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CreateAttendanceUseCaseImpl @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) : CreateAttendanceUseCase {
    override suspend fun execute(input: CreateAttendanceUseCase.Input): CreateAttendanceUseCase.Output {
        return try {
            withContext(Dispatchers.IO) {
                val result = attendanceRepository.createAttendance(input.attendance)
                when (result) {
                    is AttendanceRepository.CreateAttendanceResult.Success ->
                        CreateAttendanceUseCase.Output.Success
                    is AttendanceRepository.CreateAttendanceResult.Failure.AttendanceExists ->
                        CreateAttendanceUseCase.Output.Failure.Duplication
                    is AttendanceRepository.CreateAttendanceResult.Failure.UnknownError ->
                        CreateAttendanceUseCase.Output.Failure.Conflict(result.e.message ?: "Unknown error")
                }
            }
        } catch (e: Exception) {
            return CreateAttendanceUseCase.Output.Failure.Conflict(e.message ?: "Unknown error")
        }
    }
}