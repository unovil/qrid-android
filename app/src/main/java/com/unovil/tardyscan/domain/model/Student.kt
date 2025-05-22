package com.unovil.tardyscan.domain.model

data class Student (
    // student
    val lastName: String,
    val firstName: String,
    val middleName: String?,

    // section
    val level: Int,
    val section: String,

    // school
    val school: String
)