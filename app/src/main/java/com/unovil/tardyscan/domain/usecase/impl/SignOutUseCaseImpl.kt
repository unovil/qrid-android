package com.unovil.tardyscan.domain.usecase.impl

import com.unovil.tardyscan.data.repository.AuthenticationRepository
import com.unovil.tardyscan.domain.usecase.SignOutUseCase
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.exceptions.HttpRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignOutUseCaseImpl @Inject constructor(
  private val authenticationRepository: AuthenticationRepository
) : SignOutUseCase {
    override suspend fun execute(input: SignOutUseCase.Input): SignOutUseCase.Output = withContext(
        Dispatchers.IO
    ) {
        try {
            authenticationRepository.signOut()
            SignOutUseCase.Output.Success
        } catch (e: Exception) {
            when (e) {
                is AuthRestException -> SignOutUseCase.Output.Failure.AuthError
                is HttpRequestException -> SignOutUseCase.Output.Failure.HttpRequestError
                is HttpRequestTimeoutException -> SignOutUseCase.Output.Failure.HttpTimeoutError
                else -> SignOutUseCase.Output.Failure.Unknown(e)
            }
        }
    }
}