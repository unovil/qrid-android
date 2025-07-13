package com.unovil.tardyscan.domain.usecase.impl

import com.unovil.tardyscan.data.repository.AttendanceRepository
import com.unovil.tardyscan.domain.model.Attendance
import com.unovil.tardyscan.domain.usecase.GetAttendancesUseCase
import kotlinx.datetime.Instant
import javax.inject.Inject

class GetAttendancesUseCaseImpl @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) : GetAttendancesUseCase {
    override suspend fun execute(input: GetAttendancesUseCase.Input): GetAttendancesUseCase.Output {
        val attendanceList = mutableListOf<Attendance>()

        try {
            // Get list of students who were marked present on the given date
            val presentList = attendanceRepository.getAttendances(input.date)
            val presentListIds = presentList.map { it.studentId }.toSet() // Use Set for fast lookup

            // Get full list of students expected in class
            val allStudentIds = attendanceRepository.getAllStudentIds()

            allStudentIds.forEach { studentId ->
                val studentInfo = attendanceRepository.getStudentInfo(studentId)
                if (studentInfo != null) {
                    val isPresent = studentId in presentListIds
                    val timestamp = presentList.find { it.studentId == studentId }?.timestamp ?: Instant.fromEpochMilliseconds(0)

                    attendanceList.add(
                        Attendance(
                            studentId = studentId,
                            timestamp = timestamp,
                            name = "${studentInfo.lastName}, ${studentInfo.firstName} ${studentInfo.middleName.orEmpty()}",
                            section = "${studentInfo.section.level} - ${studentInfo.section.section}",
                            isPresent = isPresent
                        )
                    )
                }
            }

            return GetAttendancesUseCase.Output.Success(attendanceList)
        } catch (e: Exception) {
            return GetAttendancesUseCase.Output.Failure(e)
        }

    }
}