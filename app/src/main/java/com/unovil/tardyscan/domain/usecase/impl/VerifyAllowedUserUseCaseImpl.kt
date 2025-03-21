package com.unovil.tardyscan.domain.usecase.impl

import com.unovil.tardyscan.data.repository.AuthenticationRepository
import com.unovil.tardyscan.domain.usecase.VerifyAllowedUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VerifyAllowedUserUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : VerifyAllowedUserUseCase {
    override suspend fun execute(input: VerifyAllowedUserUseCase.Input): VerifyAllowedUserUseCase.Output =
        withContext(Dispatchers.IO) {
            val result = authenticationRepository.getAllowedUser(input.allowedUser)
            if (result) {
                VerifyAllowedUserUseCase.Output.Success
            } else {
                VerifyAllowedUserUseCase.Output.Failure()
            }
        }
}