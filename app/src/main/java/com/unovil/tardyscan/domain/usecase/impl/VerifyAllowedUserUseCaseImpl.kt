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
            AllowedUserResult.ERROR -> VerifyAllowedUserUseCase.Output.Failure.Conflict
            AllowedUserResult.NOT_FOUND -> VerifyAllowedUserUseCase.Output.Failure.NotFound
            AllowedUserResult.ALREADY_REGISTERED -> VerifyAllowedUserUseCase.Output.Failure.AlreadyRegistered
            AllowedUserResult.NOT_REGISTERED -> VerifyAllowedUserUseCase.Output.Success
        }
    }
}