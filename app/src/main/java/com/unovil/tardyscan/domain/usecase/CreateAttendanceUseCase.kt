package com.unovil.tardyscan.domain.usecase

import com.unovil.tardyscan.domain.model.Attendance

interface CreateAttendanceUseCase : UseCase<CreateAttendanceUseCase.Input, CreateAttendanceUseCase.Output> {
    class Input(val attendance: Attendance)

    sealed class Output {
        object Success : Output()
        open class Failure : Output() {
            object Duplication : Failure()
            data class Conflict(val message: String) : Failure()
        }
    }
}