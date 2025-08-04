package com.unovil.tardyscan.domain.model

import androidx.annotation.IntRange
import io.github.jan.supabase.storage.DownloadStatus
import kotlinx.coroutines.flow.Flow

data class Student (
    // student
    @IntRange(100_000_000_000, 999_999_999_999) val id: Long,
    val lastName: String,
    val firstName: String,
    val middleName: String?,

    // section
    val level: Int,
    val section: String,

    // school
    val school: String,

    // avatar
    val avatar: Flow<DownloadStatus>?
)