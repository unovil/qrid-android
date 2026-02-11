package com.unovil.tardyscan.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class AdminUserDto (
    @SerialName("id")
    val id: Int,

    @SerialName("domain")
    val domain: String,

    @SerialName("org_id")
    val domainId: String,

    @SerialName("name")
    val name: String?,

    @SerialName("role")
    val role: String
)