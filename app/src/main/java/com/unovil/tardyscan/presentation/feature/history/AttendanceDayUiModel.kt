package com.unovil.tardyscan.presentation.feature.history

import kotlinx.datetime.LocalDate

data class AttendanceDayUiModel(
    val date: LocalDate,
    val displayDate: String, // pre-formatted timestamp
    val presence: Presence,
    val displayTimestamp: String // pre-formatted timestamp
)
