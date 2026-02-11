package com.unovil.tardyscan.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetStudentUserRpcDto (
    @SerialName("lrn")
    val lrn: Long
)