package com.unovil.tardyscan.domain.usecase.impl

import android.util.Log
import com.unovil.tardyscan.data.repository.AttendanceRepository
import com.unovil.tardyscan.domain.model.Attendance
import com.unovil.tardyscan.domain.usecase.GetAttendancesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

class GetAttendancesUseCaseImpl @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) : GetAttendancesUseCase {
    override suspend fun execute(input: GetAttendancesUseCase.Input): GetAttendancesUseCase.Output =
        withContext(Dispatchers.IO) {
            val attendanceList = mutableListOf<Attendance>()
            Log.d("GetAttendancesUseCaseImpl", "execute with: ${input.date}")

            return@withContext try {
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

                        Log.d("GetAttendancesUseCaseImpl", "Student id: $studentId, timestamp in datetime: ${timestamp.toLocalDateTime(
                            TimeZone.currentSystemDefault())}")

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

                GetAttendancesUseCase.Output.Success(attendanceList)
            } catch (e: Exception) {
                GetAttendancesUseCase.Output.Failure(e)
            }

        }
}