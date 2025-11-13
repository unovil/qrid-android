package com.unovil.tardyscan.core.data.di

@Module
@InstallIn(SingletonComponent::class)
object CryptoModule {

    @Provides
    @Singleton
    fun provideArgon2(): Argon2Kt {
        return Argon2Kt()
    }
}