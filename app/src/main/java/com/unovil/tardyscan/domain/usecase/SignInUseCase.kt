package com.unovil.tardyscan.domain.usecase

interface SignInUseCase : UseCase<SignInUseCase.Input, SignInUseCase.Output> {
    class Input(
        val email: String,
        val password: String
    )

    sealed class Output {
        object Success : Output()
        sealed class Failure : Output() {
            object HttpTimeout : Failure()
            object HttpNetworkError : Failure()
            object AuthError : Failure()
            object Unknown : Failure()
        }
    }
}