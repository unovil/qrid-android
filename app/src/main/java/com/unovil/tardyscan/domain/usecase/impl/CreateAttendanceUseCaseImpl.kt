package com.unovil.tardyscan.domain.usecase.impl

import com.unovil.tardyscan.data.repository.AttendanceRepository
import com.unovil.tardyscan.data.repository.AttendanceRepository.CreateAttendanceResult.Failure
import com.unovil.tardyscan.data.repository.AttendanceRepository.CreateAttendanceResult.Success
import com.unovil.tardyscan.domain.usecase.CreateAttendanceUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CreateAttendanceUseCaseImpl @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) : CreateAttendanceUseCase {
    override suspend fun execute(input: CreateAttendanceUseCase.Input): CreateAttendanceUseCase.Output = withContext(
        Dispatchers.IO
    ) {
        return@withContext try {
            val result = attendanceRepository.createAttendance(input.attendance)
            when (result) {
                is Success ->
                    CreateAttendanceUseCase.Output.Success
                is Failure.AttendanceExists ->
                    CreateAttendanceUseCase.Output.Failure.Duplication
                is Failure.UnknownError ->
                    CreateAttendanceUseCase.Output.Failure.Conflict(result.e.message ?: "Unknown error")
            }
        } catch (e: Exception) {
            CreateAttendanceUseCase.Output.Failure.Conflict(e.message ?: "Unknown error")
        }
    }
}