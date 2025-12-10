package com.unovil.tardyscan.domain.usecase

import com.unovil.tardyscan.domain.model.AdministratorUser

interface VerifyAdministratorUseCase : VerifyUserUseCase<VerifyAdministratorUseCase.Input> {
    class Input(val administratorUser: AdministratorUser)
}