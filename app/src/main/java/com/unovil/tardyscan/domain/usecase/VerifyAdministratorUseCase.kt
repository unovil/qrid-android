package com.unovil.tardyscan.domain.usecase

import com.unovil.tardyscan.domain.model.SignUpAdministratorUser

interface VerifyAdministratorUseCase : VerifyUserUseCase<VerifyAdministratorUseCase.Input> {
    class Input(val signUpAdministratorUser: SignUpAdministratorUser)
}