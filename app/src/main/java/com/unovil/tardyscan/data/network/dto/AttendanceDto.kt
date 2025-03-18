package com.unovil.tardyscan.data.network.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttendanceDto (
    @SerialName("id")
    val id: Int? = null,

    @SerialName("student_id")
    val studentId: Int,

    @SerialName("date")
    val date: LocalDate,

    @SerialName("is_present")
    val isPresent: Boolean
)