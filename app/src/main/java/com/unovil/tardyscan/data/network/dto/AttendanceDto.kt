package com.unovil.tardyscan.data.network.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttendanceDto (
    @SerialName("id")
    val id: Int? = null,

    @SerialName("student_id")
    val studentId: Long,

    @SerialName("timestamp")
    val timestamp: Instant
)