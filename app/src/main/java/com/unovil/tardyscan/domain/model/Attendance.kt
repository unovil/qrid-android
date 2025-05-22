package com.unovil.tardyscan.domain.model

import kotlinx.datetime.Instant

data class Attendance (
    val studentId: Long,
    val timestamp: Instant
)