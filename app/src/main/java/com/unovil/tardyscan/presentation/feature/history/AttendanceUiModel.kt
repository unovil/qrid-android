package com.unovil.tardyscan.presentation.feature.history

data class AttendanceUiModel(
    val id: Long,
    val name: String,
    val level: Int,
    val section: String,
    val presence: Presence,
    val displayTimestamp: String // pre-formatted timestamp
)

enum class Presence { PRESENT, LATE, ABSENT }