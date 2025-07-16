package com.unovil.tardyscan.domain.usecase.impl

import com.unovil.tardyscan.data.repository.AttendanceRepository
import com.unovil.tardyscan.domain.usecase.CreateAttendanceUseCase
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CreateAttendanceUseCaseImpl @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) : CreateAttendanceUseCase {
    override suspend fun execute(input: CreateAttendanceUseCase.Input): CreateAttendanceUseCase.Output = withContext(
        Dispatchers.IO
    ) {
        try {
            attendanceRepository.createAttendance(input.attendance)
            CreateAttendanceUseCase.Output.Success
        } catch (e: Exception) {
            when (e) {
                is IllegalAccessException -> CreateAttendanceUseCase.Output.Failure.Duplication
                is HttpRequestException -> CreateAttendanceUseCase.Output.Failure.HttpRequestException
                is HttpRequestTimeoutException -> CreateAttendanceUseCase.Output.Failure.HttpRequestTimeout
                is PostgrestRestException -> CreateAttendanceUseCase.Output.Failure.PostgrestException
                else -> CreateAttendanceUseCase.Output.Failure.Unknown(e.message ?: "Unknown error")
            }
        }
    }
}