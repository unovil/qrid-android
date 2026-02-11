package com.unovil.tardyscan.domain.usecase.impl

import com.unovil.tardyscan.data.repository.AuthenticationRepository
import com.unovil.tardyscan.domain.usecase.SaveFcmTokenUseCase
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SaveFcmTokenUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : SaveFcmTokenUseCase {
    override suspend fun execute(input: SaveFcmTokenUseCase.Input): SaveFcmTokenUseCase.Output = withContext(Dispatchers.IO) {
        try {
            authenticationRepository.saveFcmToken(input.token)
            SaveFcmTokenUseCase.Output.Success
        }
        catch (e: Exception) {
            when (e) {
                is IllegalAccessException -> SaveFcmTokenUseCase.Output.Failure.Duplication
                is HttpRequestException -> SaveFcmTokenUseCase.Output.Failure.HttpRequestException
                is HttpRequestTimeoutException -> SaveFcmTokenUseCase.Output.Failure.HttpRequestTimeout
                is PostgrestRestException -> SaveFcmTokenUseCase.Output.Failure.PostgrestException
                else -> SaveFcmTokenUseCase.Output.Failure.Unknown(e.message ?: "Unknown error")
            }
        }
    }
}