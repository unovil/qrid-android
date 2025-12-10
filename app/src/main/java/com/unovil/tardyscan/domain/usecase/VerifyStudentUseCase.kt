package com.unovil.tardyscan.domain.usecase

import com.unovil.tardyscan.domain.model.StudentUser

interface VerifyStudentUseCase : VerifyUserUseCase<VerifyStudentUseCase.Input> {
    class Input(val studentUser: StudentUser)
}