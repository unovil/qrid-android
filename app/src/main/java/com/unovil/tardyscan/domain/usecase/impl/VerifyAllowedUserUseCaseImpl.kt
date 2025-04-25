package com.unovil.tardyscan.domain.usecase.impl

import com.unovil.tardyscan.data.repository.AuthenticationRepository
import com.unovil.tardyscan.data.repository.AuthenticationRepository.AllowedUserResult
import com.unovil.tardyscan.domain.usecase.VerifyAllowedUserUseCase
import de.mkammerer.argon2.Argon2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VerifyAllowedUserUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val argon2: Argon2
) : VerifyAllowedUserUseCase {
    override suspend fun execute(input: VerifyAllowedUserUseCase.Input): VerifyAllowedUserUseCase.Output = withContext(Dispatchers.IO) {
        val result = authenticationRepository.getAllowedUser(input.allowedUser)

        return@withContext when (result) {
            is AllowedUserResult.Failure.Unknown -> VerifyAllowedUserUseCase.Output.Failure.Conflict
            is AllowedUserResult.Failure.NotFound -> VerifyAllowedUserUseCase.Output.Failure.NotFound
            is AllowedUserResult.Failure.AlreadyRegistered -> VerifyAllowedUserUseCase.Output.Failure.AlreadyRegistered
            is AllowedUserResult.Success -> {
                if (argon2.verify(result.hashedPassword, input.allowedUser.givenPassword.toCharArray()))
                    VerifyAllowedUserUseCase.Output.Success
                else
                    VerifyAllowedUserUseCase.Output.Failure.NotFound
            }
        }
    }
}