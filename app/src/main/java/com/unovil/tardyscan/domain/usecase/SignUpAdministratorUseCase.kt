package com.unovil.tardyscan.domain.usecase

import com.unovil.tardyscan.domain.model.AdministratorUser

interface SignUpAdministratorUseCase : SignUpUseCase<SignUpAdministratorUseCase.Input> {
    class Input(
        val administratorUser: AdministratorUser,
        val email: String,
        val password: String
    )
}