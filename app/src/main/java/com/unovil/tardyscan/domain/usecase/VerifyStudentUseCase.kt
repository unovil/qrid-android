package com.unovil.tardyscan.domain.usecase

import com.unovil.tardyscan.domain.model.SignUpStudentUser

interface VerifyStudentUseCase : VerifyUserUseCase<VerifyStudentUseCase.Input> {
    class Input(val signUpStudentUser: SignUpStudentUser)
}