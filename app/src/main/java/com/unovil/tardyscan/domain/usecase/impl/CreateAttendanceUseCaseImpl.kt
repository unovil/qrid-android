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
                if (result) {
                    CreateAttendanceUseCase.Output.Success
                } else {
                    CreateAttendanceUseCase.Output.Failure()
                }
            }
        } catch (e: Exception) {
            return CreateAttendanceUseCase.Output.Failure.Conflict(e.message ?: "Unknown error")
        }
    }
}