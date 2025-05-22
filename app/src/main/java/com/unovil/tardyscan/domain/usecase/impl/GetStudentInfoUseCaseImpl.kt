package com.unovil.tardyscan.domain.usecase.impl

import com.unovil.tardyscan.data.repository.AttendanceRepository
import com.unovil.tardyscan.domain.model.Student
import com.unovil.tardyscan.domain.usecase.GetStudentInfoUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetStudentInfoUseCaseImpl @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) : GetStudentInfoUseCase {
    override suspend fun execute(input: GetStudentInfoUseCase.Input): GetStudentInfoUseCase.Output = withContext(Dispatchers.IO) {
        val result = attendanceRepository.getStudentInfo(input.studentId)

        if (result == null) {
            return@withContext GetStudentInfoUseCase.Output.Failure.NotFound
        }

        val student = Student(
            result.lastName,
            result.firstName,
            result.middleName,
            result.section.level,
            result.section.section,
            result.section.school.name
        )

        return@withContext GetStudentInfoUseCase.Output.Success(student)
    }
}