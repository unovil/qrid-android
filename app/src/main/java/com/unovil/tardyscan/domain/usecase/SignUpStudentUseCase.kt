package com.unovil.tardyscan.domain.usecase

import com.unovil.tardyscan.domain.model.StudentUser

interface SignUpStudentUseCase : SignUpUseCase<SignUpStudentUseCase.Input> {
    class Input(
        val studentUser: StudentUser,
        val email: String,
        val password: String
    )
}