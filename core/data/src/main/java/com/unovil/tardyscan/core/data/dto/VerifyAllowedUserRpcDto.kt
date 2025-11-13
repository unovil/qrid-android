package com.unovil.tardyscan.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class VerifyAllowedUserRpcDto (
    @SerialName("domain")
    val domain: String,

    @SerialName("org_id")
    val domainId: String
)