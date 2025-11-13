package com.unovil.tardyscan.core.domain

import com.unovil.tardyscan.domain.model.Student

interface GetStudentInfoUseCase : UseCase<GetStudentInfoUseCase.Input, GetStudentInfoUseCase.Output> {
    class Input (val qrCode: String)

    sealed class Output {
        class Success(val student: Student) : Output()
        open class Failure : Output() {
            object InvalidCode : Failure()
            object InvalidDecryption : Failure()
            object NotFound : Failure()
            object HttpRequestError : Failure()
            object HttpRequestTimeout : Failure()
            data class PostgrestError(val message: String) : Failure()
            data class UnknownError(val message: String) : Failure()
        }
    }
}