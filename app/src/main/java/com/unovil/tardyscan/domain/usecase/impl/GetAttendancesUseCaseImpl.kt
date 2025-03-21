package com.unovil.tardyscan.domain.usecase.impl

import com.unovil.tardyscan.data.repository.AttendanceRepository
import com.unovil.tardyscan.domain.model.Attendance
import com.unovil.tardyscan.domain.usecase.GetAttendancesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAttendancesUseCaseImpl @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) : GetAttendancesUseCase {
    override suspend fun execute(input: Unit): GetAttendancesUseCase.Output =
        withContext(Dispatchers.IO) {
            val result = attendanceRepository.getAttendances()
            return@withContext result?.let {
                GetAttendancesUseCase.Output.Success(data = it.map {
                    Attendance(
                        id = it.id,
                        studentId = it.studentId,
                        date = it.date,
                        isPresent = it.isPresent
                    )
                })
            } ?: GetAttendancesUseCase.Output.Failure
        }
}