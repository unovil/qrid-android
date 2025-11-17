package com.unovil.tardyscan.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class AttendanceDto @OptIn(ExperimentalTime::class) constructor(
    @SerialName("id")
    val id: Int? = null,

    @SerialName("student_id")
    val studentId: Long,

    @SerialName("timestamp")
    val timestamp: Instant,

    @SerialName("allowed_user_id")
    val senderId: Int? = null
)