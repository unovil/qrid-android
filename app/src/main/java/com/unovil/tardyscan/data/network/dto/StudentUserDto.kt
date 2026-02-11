package com.unovil.tardyscan.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class StudentUserDto (
    @SerialName("id")
    val id: Int,

    @SerialName("lrn")
    val lrn: Long
)