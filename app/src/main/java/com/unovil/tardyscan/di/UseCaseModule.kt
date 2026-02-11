package com.unovil.tardyscan.di

import com.unovil.tardyscan.domain.usecase.CreateAttendanceUseCase
import com.unovil.tardyscan.domain.usecase.GetAttendancesUseCase
import com.unovil.tardyscan.domain.usecase.GetSignedUserUseCase
import com.unovil.tardyscan.domain.usecase.GetStudentInfoUseCase
import com.unovil.tardyscan.domain.usecase.SaveFcmTokenUseCase
import com.unovil.tardyscan.domain.usecase.SignInUseCase
import com.unovil.tardyscan.domain.usecase.SignOutUseCase
import com.unovil.tardyscan.domain.usecase.SignUpAdministratorUseCase
import com.unovil.tardyscan.domain.usecase.SignUpStudentUseCase
import com.unovil.tardyscan.domain.usecase.VerifyAdministratorUseCase
import com.unovil.tardyscan.domain.usecase.VerifyStudentUseCase
import com.unovil.tardyscan.domain.usecase.impl.CreateAttendanceUseCaseImpl
import com.unovil.tardyscan.domain.usecase.impl.GetAttendancesUseCaseImpl
import com.unovil.tardyscan.domain.usecase.impl.GetSignedUserUseCaseImpl
import com.unovil.tardyscan.domain.usecase.impl.GetStudentInfoUseCaseImpl
import com.unovil.tardyscan.domain.usecase.impl.SaveFcmTokenUseCaseImpl
import com.unovil.tardyscan.domain.usecase.impl.SignInUseCaseImpl
import com.unovil.tardyscan.domain.usecase.impl.SignOutUseCaseImpl
import com.unovil.tardyscan.domain.usecase.impl.SignUpAdministratorUseCaseImpl
import com.unovil.tardyscan.domain.usecase.impl.SignUpStudentUseCaseImpl
import com.unovil.tardyscan.domain.usecase.impl.VerifyAdministratorUseCaseImpl
import com.unovil.tardyscan.domain.usecase.impl.VerifyStudentUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Suppress("unused")
@InstallIn(SingletonComponent::class)
@Module
abstract class UseCaseModule {
    @Binds
    abstract fun bindGetStudentInfoUseCase(impl: GetStudentInfoUseCaseImpl): GetStudentInfoUseCase

    @Binds
    abstract fun bindVerifyAdministratorUseCase(impl: VerifyAdministratorUseCaseImpl): VerifyAdministratorUseCase

    @Binds
    abstract fun bindVerifyStudentUseCase(impl: VerifyStudentUseCaseImpl): VerifyStudentUseCase

    @Binds
    abstract fun bindCreateAttendanceUseCase(impl: CreateAttendanceUseCaseImpl): CreateAttendanceUseCase

    @Binds
    abstract fun bindSignUpAdministratorUseCase(impl: SignUpAdministratorUseCaseImpl): SignUpAdministratorUseCase

    @Binds
    abstract fun bindSignUpStudentUseCase(impl: SignUpStudentUseCaseImpl): SignUpStudentUseCase

    @Binds
    abstract fun bindSignInUseCase(impl: SignInUseCaseImpl): SignInUseCase

    @Binds
    abstract fun bindSignOutUseCase(impl: SignOutUseCaseImpl): SignOutUseCase

    @Binds
    abstract fun bindGetAttendancesUseCase(impl: GetAttendancesUseCaseImpl): GetAttendancesUseCase

    @Binds
    abstract fun bindGetSignedUserUseCase(impl: GetSignedUserUseCaseImpl): GetSignedUserUseCase

    @Binds
    abstract fun bindSaveFcmTokenUseCase(impl: SaveFcmTokenUseCaseImpl): SaveFcmTokenUseCase

}