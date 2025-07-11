package com.unovil.tardyscan.domain.usecase.impl

import com.unovil.tardyscan.data.repository.AuthenticationRepository
import com.unovil.tardyscan.data.repository.AuthenticationRepository.SignOutResult.Failure
import com.unovil.tardyscan.data.repository.AuthenticationRepository.SignOutResult.Success
import com.unovil.tardyscan.domain.usecase.SignOutUseCase
import javax.inject.Inject

class SignOutUseCaseImpl @Inject constructor(
  private val authenticationRepository: AuthenticationRepository
) : SignOutUseCase {
    override suspend fun execute(input: SignOutUseCase.Input): SignOutUseCase.Output {
        return when (val result = authenticationRepository.signOut()) {
            is Success -> SignOutUseCase.Output.Success
            is Failure.AuthError -> SignOutUseCase.Output.Failure.AuthError
            is Failure.HttpTimeout -> SignOutUseCase.Output.Failure.HttpTimeoutError
            is Failure.HttpError -> SignOutUseCase.Output.Failure.HttpRequestError
            is Failure.Unknown -> SignOutUseCase.Output.Failure.Unknown(result.error)
        }
    }
}