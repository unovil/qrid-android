package com.unovil.tardyscan.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.unovil.tardyscan.data.local.dao.AttendanceDao
import com.unovil.tardyscan.data.local.entity.AttendanceEntity

@Database(entities = [AttendanceEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun attendanceDao(): AttendanceDao
}