package com.unovil.tardyscan.domain.usecase

import com.unovil.tardyscan.domain.model.AllowedUser

interface SignUpUseCase : UseCase<SignUpUseCase.Input, SignUpUseCase.Output> {
    class Input(
        val allowedUser: AllowedUser,
        val email: String,
        val password: String
    )

    sealed class Output {
        object Success : Output()
        open class Failure : Output() {
            object Unverified : Failure()
            class WeakPassword(val reasons: List<String>) : Failure()
            object Unknown : Failure()
        }
    }
}