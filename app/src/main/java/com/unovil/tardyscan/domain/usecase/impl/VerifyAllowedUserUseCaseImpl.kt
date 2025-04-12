package com.unovil.tardyscan.domain.usecase.impl

import com.unovil.tardyscan.data.repository.AuthenticationRepository
import com.unovil.tardyscan.data.repository.AuthenticationRepository.AllowedUserResult
import com.unovil.tardyscan.domain.usecase.VerifyAllowedUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VerifyAllowedUserUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : VerifyAllowedUserUseCase {
    override suspend fun execute(input: VerifyAllowedUserUseCase.Input): VerifyAllowedUserUseCase.Output = withContext(Dispatchers.IO) {
        val result = authenticationRepository.getAllowedUser(input.allowedUser)

        when (result) {
            is AllowedUserResult.Failure.Unknown -> VerifyAllowedUserUseCase.Output.Failure.Conflict
            is AllowedUserResult.Failure.NotFound -> VerifyAllowedUserUseCase.Output.Failure.NotFound
            is AllowedUserResult.Failure.AlreadyRegistered -> VerifyAllowedUserUseCase.Output.Failure.AlreadyRegistered
            is AllowedUserResult.Success -> VerifyAllowedUserUseCase.Output.Success
        }
    }
}