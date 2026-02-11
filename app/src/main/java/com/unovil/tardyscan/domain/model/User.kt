package com.unovil.tardyscan.domain.model

sealed class User {
    data class Administrator(val admin: com.unovil.tardyscan.domain.model.Administrator) : User()
    data class Student(val student: com.unovil.tardyscan.domain.model.Student) : User()
}