package com.unovil.tardyscan.domain.usecase.impl

import com.unovil.tardyscan.data.repository.AttendanceRepository
import com.unovil.tardyscan.domain.usecase.GetAttendancesUseCase
import javax.inject.Inject

class GetAttendancesUseCaseImpl @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) : GetAttendancesUseCase {
    override fun execute(input: GetAttendancesUseCase.Input): GetAttendancesUseCase.Output {
        try {
            val attendanceFlow = attendanceRepository.getAttendances()
            return GetAttendancesUseCase.Output.Success(attendanceFlow)
        } catch (e: Exception) {
            return GetAttendancesUseCase.Output.Failure(e)
        }
    }
}