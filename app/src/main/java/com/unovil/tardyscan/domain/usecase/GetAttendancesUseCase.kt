package com.unovil.tardyscan.domain.usecase

import com.unovil.tardyscan.data.local.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow

interface GetAttendancesUseCase : AsyncUseCase<GetAttendancesUseCase.Input, GetAttendancesUseCase.Output> {
    class Input

    sealed class Output {
        data class Success(val attendanceFlow: Flow<List<AttendanceEntity>>) : Output()
        class Failure(val e: Exception) : Output()
    }
}