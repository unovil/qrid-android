package com.unovil.tardyscan.domain.usecase

import com.unovil.tardyscan.domain.model.Attendance
import kotlinx.datetime.LocalDate

interface GetAttendancesUseCase : UseCase<GetAttendancesUseCase.Input, GetAttendancesUseCase.Output> {
    data class Input(val date: LocalDate)

    sealed class Output {
        data class Success(val attendanceList: List<Attendance>) : Output()
        class Failure(val e: Exception) : Output()
    }
}