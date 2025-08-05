package com.unovil.tardyscan.domain.usecase

interface GetSignedUserUseCase : UseCase<GetSignedUserUseCase.Input, GetSignedUserUseCase.Output> {
    class Input

    sealed class Output {
        object Success : Output()
        open class Failure : Output() {
            object PostgrestException : Failure()
            object HttpRequestException : Failure()
            object HttpRequestTimeout : Failure()
            data class Unknown(val throwable: Throwable) : Failure()
        }
    }
}