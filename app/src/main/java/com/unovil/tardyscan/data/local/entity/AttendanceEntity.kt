package com.unovil.tardyscan.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("section")
    val section: String,

    @ColumnInfo("lrn")
    val lrn: Long,

    @ColumnInfo("timestamp")
    val timestamp: Long

)