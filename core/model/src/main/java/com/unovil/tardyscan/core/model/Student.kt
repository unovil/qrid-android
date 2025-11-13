package com.unovil.tardyscan.core.model

data class Student (
    // student
    val id: Long, // from 100_000_000_000 to 999_999_999_999
    val lastName: String,
    val firstName: String,
    val middleName: String?,

    // section
    val level: Int,
    val section: String,

    // school
    val school: String,

    // avatar
    val avatarUrl: String
)