package com.unovil.tardyscan.core.domain.impl

import com.unovil.tardyscan.core.data.repository.AuthenticationRepository
import com.unovil.tardyscan.core.domain.SignUpUseCase
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.exception.AuthWeakPasswordException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignUpUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : SignUpUseCase {
    override suspend fun execute(input: SignUpUseCase.Input): SignUpUseCase.Output = withContext(
        Dispatchers.IO
    ) {
        try {
            authenticationRepository.signUp(
                input.allowedUser, input.email, input.password
            )
            SignUpUseCase.Output.Success
        } catch (e: Exception) {
            when (e) {
                is IllegalAccessException -> SignUpUseCase.Output.Failure.Unverified
                is AuthWeakPasswordException -> SignUpUseCase.Output.Failure.WeakPassword(e.reasons)
                is AuthRestException -> {
                    if (e.errorCode == AuthErrorCode.UserAlreadyExists) {
                        SignUpUseCase.Output.Failure.AlreadyExists
                    } else {
                        SignUpUseCase.Output.Failure.Unknown

                    }
                }
                else -> SignUpUseCase.Output.Failure.Unknown
            }
        }
    }
}