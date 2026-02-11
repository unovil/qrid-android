package com.unovil.tardyscan.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetAdminUserRpcDto (
    @SerialName("domain")
    val domain: String,

    @SerialName("org_id")
    val domainId: String
)