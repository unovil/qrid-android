package com.unovil.tardyscan.domain.usecase

import com.unovil.tardyscan.domain.model.Attendance
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth

interface GetAttendancesUseCase : UseCase<GetAttendancesUseCase.Input, GetAttendancesUseCase.Output> {
    sealed class Input {
        data class Admin(val date: LocalDate) : Input()
        data class Student(val month: YearMonth) : Input()
    }

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