package com.unovil.tardyscan.core.domain

interface SignOutUseCase : UseCase<SignOutUseCase.Input, SignOutUseCase.Output> {
    class Input()

    sealed class Output {
        object Success : Output()
        sealed class Failure : Output() {
            object AuthError : Failure()
            object HttpRequestError : Failure()
            object HttpTimeoutError : Failure()
            data class Unknown(val error: Throwable) : Failure()
        }
    }
}