package com.unovil.tardyscan.domain.usecase

interface SaveFcmTokenUseCase : UseCase<SaveFcmTokenUseCase.Input, SaveFcmTokenUseCase.Output> {
    class Input(val token: String)

    sealed class Output {
        object Success : Output()
        open class Failure : Output() {
            object Duplication : Failure()
            object PostgrestException : Failure()
            object HttpRequestException : Failure()
            object HttpRequestTimeout : Failure()
            data class Unknown(val message: String) : Failure()
        }
    }
}