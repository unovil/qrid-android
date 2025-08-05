package com.unovil.tardyscan.domain.usecase.impl

import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2Mode
import com.unovil.tardyscan.data.repository.AuthenticationRepository
import com.unovil.tardyscan.data.repository.AuthenticationRepository.AllowedUserResult.Failure
import com.unovil.tardyscan.data.repository.AuthenticationRepository.AllowedUserResult.Success
import com.unovil.tardyscan.domain.usecase.VerifyAllowedUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VerifyAllowedUserUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val argon2: Argon2Kt
) : VerifyAllowedUserUseCase {
    override suspend fun execute(input: VerifyAllowedUserUseCase.Input): VerifyAllowedUserUseCase.Output = withContext(Dispatchers.IO) {
        val result = authenticationRepository.getAllowedUserResult(input.allowedUser)

        return@withContext when (result) {
            is Failure.Unknown -> VerifyAllowedUserUseCase.Output.Failure.Conflict
            is Failure.NotFound -> VerifyAllowedUserUseCase.Output.Failure.NotFound
            is Failure.AlreadyRegistered -> VerifyAllowedUserUseCase.Output.Failure.AlreadyRegistered
            is Success -> {
                if (argon2.verify(Argon2Mode.ARGON2_I,result.hashedPassword, input.allowedUser.givenPassword.toByteArray()))
                    VerifyAllowedUserUseCase.Output.Success
                else
                    VerifyAllowedUserUseCase.Output.Failure.NotFound
            }
        }
    }
}