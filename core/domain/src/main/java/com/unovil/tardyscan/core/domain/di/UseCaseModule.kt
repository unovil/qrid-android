package com.unovil.tardyscan.core.domain.di

import com.unovil.tardyscan.core.domain.CreateAttendanceUseCase
import com.unovil.tardyscan.core.domain.GetAttendancesUseCase
import com.unovil.tardyscan.core.domain.GetSignedUserUseCase
import com.unovil.tardyscan.core.domain.GetStudentAvatarUseCase
import com.unovil.tardyscan.core.domain.GetStudentInfoUseCase
import com.unovil.tardyscan.core.domain.SignInUseCase
import com.unovil.tardyscan.core.domain.SignOutUseCase
import com.unovil.tardyscan.core.domain.SignUpUseCase
import com.unovil.tardyscan.core.domain.VerifyAllowedUserUseCase
import com.unovil.tardyscan.core.domain.impl.CreateAttendanceUseCaseImpl
import com.unovil.tardyscan.core.domain.impl.GetAttendancesUseCaseImpl
import com.unovil.tardyscan.core.domain.impl.GetSignedUserUseCaseImpl
import com.unovil.tardyscan.core.domain.impl.GetStudentAvatarUseCaseImpl
import com.unovil.tardyscan.core.domain.impl.GetStudentInfoUseCaseImpl
import com.unovil.tardyscan.core.domain.impl.SignInUseCaseImpl
import com.unovil.tardyscan.core.domain.impl.SignOutUseCaseImpl
import com.unovil.tardyscan.core.domain.impl.SignUpUseCaseImpl
import com.unovil.tardyscan.core.domain.impl.VerifyAllowedUserUseCaseImpl
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

    @Binds
    abstract fun bindGetStudentAvatarUseCase(impl: GetStudentAvatarUseCaseImpl): GetStudentAvatarUseCase

}