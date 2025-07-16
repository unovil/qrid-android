package com.unovil.tardyscan.domain.usecase

import com.unovil.tardyscan.domain.model.Attendance
import kotlinx.datetime.LocalDate

interface GetAttendancesUseCase : UseCase<GetAttendancesUseCase.Input, GetAttendancesUseCase.Output> {
    data class Input(val date: LocalDate)

    sealed class Output {
        data class Success(val attendanceList: List<Attendance>) : Output()
        open class Failure : Output() {
            object PostgrestException : Failure()
            object HttpRequestException : Failure()
            object HttpRequestTimeout : Failure()
            data class Unknown(val throwable: Throwable) : Failure()
        }
    }
}