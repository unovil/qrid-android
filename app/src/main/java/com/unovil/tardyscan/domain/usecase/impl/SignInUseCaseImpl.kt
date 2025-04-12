package com.unovil.tardyscan.domain.usecase.impl

import com.unovil.tardyscan.data.repository.AuthenticationRepository
import com.unovil.tardyscan.domain.usecase.SignInUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInUseCaseImpl @Inject constructor(
    val authenticationRepository: AuthenticationRepository
) : SignInUseCase {
    override suspend fun execute(input: SignInUseCase.Input): SignInUseCase.Output = withContext(Dispatchers.IO) {
        val result = authenticationRepository.signIn(input.email, input.password)

        when (result) {
            is AuthenticationRepository.SignInResult.Success -> SignInUseCase.Output.Success
            is AuthenticationRepository.SignInResult.Failure.HttpNetworkError -> SignInUseCase.Output.Failure.HttpNetworkError
            is AuthenticationRepository.SignInResult.Failure.HttpTimeout -> SignInUseCase.Output.Failure.HttpTimeout
            is AuthenticationRepository.SignInResult.Failure.InvalidCredentials -> SignInUseCase.Output.Failure.AuthError
            is AuthenticationRepository.SignInResult.Failure.Unknown -> SignInUseCase.Output.Failure.Unknown
        }
    }
}