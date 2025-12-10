package com.unovil.tardyscan.domain.usecase

import com.unovil.tardyscan.domain.model.SignUpStudentUser

interface SignUpStudentUseCase : SignUpUseCase<SignUpStudentUseCase.Input> {
    class Input(
        val signUpStudentUser: SignUpStudentUser,
        val email: String,
        val password: String
    )
}