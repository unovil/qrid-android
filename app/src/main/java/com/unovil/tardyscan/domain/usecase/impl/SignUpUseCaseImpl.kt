package com.unovil.tardyscan.domain.usecase.impl

import com.unovil.tardyscan.data.repository.AuthenticationRepository
import com.unovil.tardyscan.data.repository.AuthenticationRepository.SignUpResult
import com.unovil.tardyscan.domain.usecase.SignUpUseCase
import com.unovil.tardyscan.domain.usecase.SignUpUseCase.Output.Failure.Unknown
import com.unovil.tardyscan.domain.usecase.SignUpUseCase.Output.Failure.Unverified
import com.unovil.tardyscan.domain.usecase.SignUpUseCase.Output.Failure.WeakPassword
import com.unovil.tardyscan.domain.usecase.SignUpUseCase.Output.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignUpUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : SignUpUseCase {
    override suspend fun execute(input: SignUpUseCase.Input): SignUpUseCase.Output = withContext(Dispatchers.IO) {
        val result = authenticationRepository.signUp(
            input.allowedUser, input.email, input.password
        )

        when (result) {
            is SignUpResult.Success -> Success
            is SignUpResult.Failure.Unverified -> Unverified
            is SignUpResult.Failure.WeakPassword -> WeakPassword(result.reasons)
            is SignUpResult.Failure -> Unknown
        }
    }
}