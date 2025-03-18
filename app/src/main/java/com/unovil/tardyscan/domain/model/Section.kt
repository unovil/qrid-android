package com.unovil.tardyscan.domain.model

data class Section (
    val id: Int? = null,
    val level: Int,
    val section: String,
    val schoolId: Int
)