package com.unovil.tardyscan.domain.usecase.impl

import com.unovil.tardyscan.data.repository.AuthenticationRepository
import com.unovil.tardyscan.domain.usecase.SignInUseCase
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.exceptions.HttpRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInUseCaseImpl @Inject constructor(
    val authenticationRepository: AuthenticationRepository
) : SignInUseCase {
    override suspend fun execute(input: SignInUseCase.Input): SignInUseCase.Output = withContext(Dispatchers.IO) {
        try {
            authenticationRepository.signIn(input.email, input.password)
            SignInUseCase.Output.Success
        } catch (e: Exception) {
            when (e) {
                is HttpRequestTimeoutException -> SignInUseCase.Output.Failure.HttpTimeout
                is HttpRequestException -> SignInUseCase.Output.Failure.HttpNetworkError
                is AuthRestException -> {
                    if (e.errorCode == AuthErrorCode.InvalidCredentials) {
                        SignInUseCase.Output.Failure.AuthError
                    } else {
                        SignInUseCase.Output.Failure.Unknown
                    }
                }
                else -> SignInUseCase.Output.Failure.Unknown
            }
        }
    }
}