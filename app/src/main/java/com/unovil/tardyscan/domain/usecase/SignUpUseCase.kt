package com.unovil.tardyscan.domain.usecase

interface SignUpUseCase<Input: Any> : UseCase<Input, SignUpUseCase.Output> {
    sealed class Output {
        object Success : Output()
        open class Failure : Output() {
            object Unverified : Failure()
            class WeakPassword(val reasons: List<String>) : Failure()
            object AlreadyExists : Failure()
            object Unknown : Failure()
        }
    }
}