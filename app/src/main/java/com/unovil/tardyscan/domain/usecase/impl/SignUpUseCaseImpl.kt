package com.unovil.tardyscan.domain.usecase.impl

import com.unovil.tardyscan.data.repository.AuthenticationRepository
import com.unovil.tardyscan.data.repository.AuthenticationRepository.SignUpResult.Failure
import com.unovil.tardyscan.data.repository.AuthenticationRepository.SignUpResult.Success
import com.unovil.tardyscan.domain.usecase.SignUpUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignUpUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : SignUpUseCase {
    override suspend fun execute(input: SignUpUseCase.Input): SignUpUseCase.Output = withContext(
        Dispatchers.IO
    ) {
        val result = authenticationRepository.signUp(
            input.allowedUser, input.email, input.password
        )

        when (result) {
            is Success -> SignUpUseCase.Output.Success
            is Failure.Unverified -> SignUpUseCase.Output.Failure.Unverified
            is Failure.WeakPassword -> SignUpUseCase.Output.Failure.WeakPassword(result.reasons)
            is Failure.AlreadyExists -> SignUpUseCase.Output.Failure.AlreadyExists
            is Failure -> SignUpUseCase.Output.Failure.Unknown
        }
    }
}