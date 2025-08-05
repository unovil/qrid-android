package com.unovil.tardyscan.di

import com.unovil.tardyscan.domain.usecase.CreateAttendanceUseCase
import com.unovil.tardyscan.domain.usecase.GetAttendancesUseCase
import com.unovil.tardyscan.domain.usecase.GetSignedUserUseCase
import com.unovil.tardyscan.domain.usecase.GetStudentInfoUseCase
import com.unovil.tardyscan.domain.usecase.SignInUseCase
import com.unovil.tardyscan.domain.usecase.SignOutUseCase
import com.unovil.tardyscan.domain.usecase.SignUpUseCase
import com.unovil.tardyscan.domain.usecase.VerifyAllowedUserUseCase
import com.unovil.tardyscan.domain.usecase.impl.CreateAttendanceUseCaseImpl
import com.unovil.tardyscan.domain.usecase.impl.GetAttendancesUseCaseImpl
import com.unovil.tardyscan.domain.usecase.impl.GetSignedUserUseCaseImpl
import com.unovil.tardyscan.domain.usecase.impl.GetStudentInfoUseCaseImpl
import com.unovil.tardyscan.domain.usecase.impl.SignInUseCaseImpl
import com.unovil.tardyscan.domain.usecase.impl.SignOutUseCaseImpl
import com.unovil.tardyscan.domain.usecase.impl.SignUpUseCaseImpl
import com.unovil.tardyscan.domain.usecase.impl.VerifyAllowedUserUseCaseImpl
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
    abstract fun bindVerifyAllowedUserUseCase(impl: VerifyAllowedUserUseCaseImpl): VerifyAllowedUserUseCase

    @Binds
    abstract fun bindCreateAttendanceUseCase(impl: CreateAttendanceUseCaseImpl): CreateAttendanceUseCase

    @Binds
    abstract fun bindSignUpUseCase(impl: SignUpUseCaseImpl): SignUpUseCase

    @Binds
    abstract fun bindSignInUseCase(impl: SignInUseCaseImpl): SignInUseCase

    @Binds
    abstract fun bindSignOutUseCase(impl: SignOutUseCaseImpl): SignOutUseCase

    @Binds
    abstract fun bindGetAttendancesUseCase(impl: GetAttendancesUseCaseImpl): GetAttendancesUseCase

    @Binds
    abstract fun bindGetSignedUserUseCase(impl: GetSignedUserUseCaseImpl): GetSignedUserUseCase

}