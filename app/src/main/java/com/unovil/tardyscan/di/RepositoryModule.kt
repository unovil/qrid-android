package com.unovil.tardyscan.di

import com.unovil.tardyscan.data.repository.AttendanceRepository
import com.unovil.tardyscan.data.repository.impl.AttendanceRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {
    @Binds
    abstract fun bindAttendanceRepository(impl: AttendanceRepositoryImpl): AttendanceRepository
}