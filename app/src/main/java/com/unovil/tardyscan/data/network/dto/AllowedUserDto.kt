package com.unovil.tardyscan.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class AllowedUserDto (
    @SerialName("id")
    val id: Int? = null,

    @SerialName("domain")
    val domain: String,

    @SerialName("org_id")
    val domainId: String,

    @SerialName("given_password")
    val givenPassword: String,

    @SerialName("is_registered")
    val isRegistered: Boolean? = false
)