package com.unovil.tardyscan.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SchoolDto (
    @SerialName("id")
    val id: Int? = null,

    @SerialName("name")
    val name: String,

    @SerialName("domain")
    val domain: String
)