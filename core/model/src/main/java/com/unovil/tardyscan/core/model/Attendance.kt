package com.unovil.tardyscan.core.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class Attendance @OptIn(ExperimentalTime::class) constructor(
    val studentId: Long,
    val timestamp: Instant,

    // for receiving
    val name: String = "",
    val level: Int = 0,
    val section: String = "",
    val isPresent: Boolean = false
)