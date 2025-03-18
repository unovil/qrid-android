package com.unovil.tardyscan.domain.model

data class Student (
    val id: Int? = null,
    val lastName: String,
    val firstName: String,
    val middleName: String?,
    val sectionId: Int
)