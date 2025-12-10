package com.unovil.tardyscan.domain.usecase.impl

import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2Mode
import com.unovil.tardyscan.data.repository.AuthenticationRepository
import com.unovil.tardyscan.data.repository.AuthenticationRepository.UserRpcResult.Failure
import com.unovil.tardyscan.data.repository.AuthenticationRepository.UserRpcResult.Success
import com.unovil.tardyscan.domain.usecase.VerifyAdministratorUseCase
import com.unovil.tardyscan.domain.usecase.VerifyUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VerifyAdministratorUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val argon2: Argon2Kt
) : VerifyAdministratorUseCase {
    override suspend fun execute(input: VerifyAdministratorUseCase.Input): VerifyUserUseCase.Output = withContext(Dispatchers.IO) {
        val result = authenticationRepository.getUserResult(input.administratorUser)

        return@withContext when (result) {
            is Failure.Unknown -> VerifyUserUseCase.Output.Failure.Conflict
            is Failure.NotFound -> VerifyUserUseCase.Output.Failure.NotFound
            is Failure.AlreadyRegistered -> VerifyUserUseCase.Output.Failure.AlreadyRegistered
            is Success -> {
                if (argon2.verify(Argon2Mode.ARGON2_I,result.hashedPassword, input.administratorUser.givenPassword.toByteArray()))
                    VerifyUserUseCase.Output.Success
                else
                    VerifyUserUseCase.Output.Failure.NotFound
            }
        }
    }
}