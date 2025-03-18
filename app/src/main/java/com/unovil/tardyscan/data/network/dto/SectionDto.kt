package com.unovil.tardyscan.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SectionDto (
    @SerialName("id")
    val id: Int? = null,

    @SerialName("level")
    val level: Int,

    @SerialName("section")
    val section: String,

    @SerialName("school_id")
    val schoolId: Int
)