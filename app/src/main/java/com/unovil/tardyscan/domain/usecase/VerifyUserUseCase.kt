package com.unovil.tardyscan.domain.usecase

interface VerifyUserUseCase<Input: Any>: UseCase<Input, VerifyUserUseCase.Output> {
    sealed class Output {
        object Success : Output()
        open class Failure : Output() {
            object AlreadyRegistered : Failure()
            object NotFound : Failure()
            object Conflict : Failure()
        }
    }
}