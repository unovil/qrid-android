package com.unovil.tardyscan.domain.model

data class AllowedUser (
    val id: Int? = null,
    val domain: String,
    val domainId: String,
    val givenPassword: String,
    val isRegistered: Boolean? = false
)