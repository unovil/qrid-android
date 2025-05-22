package com.unovil.tardyscan.domain.usecase

import androidx.annotation.IntRange
import com.unovil.tardyscan.domain.model.Student

interface GetStudentInfoUseCase : UseCase<GetStudentInfoUseCase.Input, GetStudentInfoUseCase.Output> {
    class Input (@IntRange(100_000_000_000, 999_999_999_999) val studentId: Long)

    sealed class Output {
        class Success(val student: Student) : Output()
        open class Failure : Output() {
            object NotFound : Failure()
        }
    }
}