package com.unovil.tardyscan.domain.model

import kotlinx.datetime.LocalDate

data class Attendance (
    val id: Int? = null,
    val studentId: Int,
    val date: LocalDate,
    val isPresent: Boolean = true
)