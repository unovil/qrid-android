package com.unovil.tardyscan.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.unovil.tardyscan.data.local.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Insert
    fun insertAttendance(attendance: AttendanceEntity)

    @Query("SELECT * FROM attendance ORDER BY timestamp DESC")
    fun getAttendances(): Flow<List<AttendanceEntity>>
}