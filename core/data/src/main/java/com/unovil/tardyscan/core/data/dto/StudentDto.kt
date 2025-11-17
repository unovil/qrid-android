package com.unovil.tardyscan.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentDto (
    @SerialName("id")
    val id: Long? = null,

    @SerialName("last_name")
    val lastName: String,

    @SerialName("first_name")
    val firstName: String,

    @SerialName("middle_name")
    val middleName: String?,

    @SerialName("avatar")
    val avatarUrl: String? = null,

    @SerialName("sections")
    val section: SectionDto
)