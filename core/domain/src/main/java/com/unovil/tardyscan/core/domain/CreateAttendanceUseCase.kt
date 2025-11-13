package com.unovil.tardyscan.core.domain

import com.unovil.tardyscan.core.model.Attendance

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