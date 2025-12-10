package com.unovil.tardyscan.domain.usecase

import com.unovil.tardyscan.domain.model.SignUpAdministratorUser

interface SignUpAdministratorUseCase : SignUpUseCase<SignUpAdministratorUseCase.Input> {
    class Input(
        val signUpAdministratorUser: SignUpAdministratorUser,
        val email: String,
        val password: String
    )
}