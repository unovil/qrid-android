package com.unovil.tardyscan.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@ExperimentalTime
data class UserDeviceDto (
    @SerialName("fcm_token")
    val fcmToken: String,

    @SerialName("created_at")
    val timestamp: Instant,

    @SerialName("student_user_id")
    val studentUserId: Int
)
