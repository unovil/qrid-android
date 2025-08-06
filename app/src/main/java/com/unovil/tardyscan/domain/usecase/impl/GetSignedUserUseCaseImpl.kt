package com.unovil.tardyscan.domain.usecase.impl

import android.util.Log
import com.unovil.tardyscan.data.repository.AuthenticationRepository
import com.unovil.tardyscan.domain.usecase.GetSignedUserUseCase
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetSignedUserUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
) : GetSignedUserUseCase {
    override suspend fun execute(input: GetSignedUserUseCase.Input): GetSignedUserUseCase.Output =
        withContext(Dispatchers.IO) {
            try {
                authenticationRepository.updateAllowedUser()

                GetSignedUserUseCase.Output.Success
            } catch (e: Exception) {
                Log.e("GetSignedUserUseCaseImpl", "Error fetching signed user: ${e.message}")
                when (e) {
                    is PostgrestRestException -> GetSignedUserUseCase.Output.Failure.PostgrestException
                    is HttpRequestException -> GetSignedUserUseCase.Output.Failure.HttpRequestException
                    is HttpRequestTimeoutException -> GetSignedUserUseCase.Output.Failure.HttpRequestTimeout
                    else -> GetSignedUserUseCase.Output.Failure.Unknown(e)
                }
            }
        }
}