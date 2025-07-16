package com.unovil.tardyscan.domain.usecase

import com.unovil.tardyscan.domain.model.Attendance

interface CreateAttendanceUseCase : UseCase<CreateAttendanceUseCase.Input, CreateAttendanceUseCase.Output> {
    class Input(val attendance: Attendance)

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