package com.unovil.tardyscan.domain.model

import kotlinx.datetime.Instant

data class Attendance (
    val studentId: Long,
    val timestamp: Instant,

    // for receiving
    val name: String = "",
    val level: Int = 0,
    val section: String = "",
    val isPresent: Boolean = false
)