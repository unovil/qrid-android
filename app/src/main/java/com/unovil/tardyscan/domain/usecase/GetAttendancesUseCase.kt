package com.unovil.tardyscan.domain.usecase

import com.unovil.tardyscan.domain.model.Attendance

interface GetAttendancesUseCase : UseCase<Unit, GetAttendancesUseCase.Output> {
    sealed class Output {
        class Success(val data: List<Attendance>) : Output()
        object Failure : Output()
    }
}