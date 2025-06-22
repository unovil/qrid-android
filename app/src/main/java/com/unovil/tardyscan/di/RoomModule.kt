package com.unovil.tardyscan.di

import android.content.Context
import androidx.room.Room
import com.unovil.tardyscan.data.local.dao.AttendanceDao
import com.unovil.tardyscan.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    fun provideRoom(@ApplicationContext context: Context): AppDatabase {
        return Room
            .databaseBuilder(context, AppDatabase::class.java, "app_db")
            .build()
    }

    @Provides
    fun provideAttendanceDao(db: AppDatabase): AttendanceDao {
        return db.attendanceDao()
    }
}