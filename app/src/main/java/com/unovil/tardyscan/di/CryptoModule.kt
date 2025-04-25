package com.unovil.tardyscan.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CryptoModule {

    @Provides
    @Singleton
    fun provideArgon2(): Argon2 {
        return Argon2Factory.create()
    }
}