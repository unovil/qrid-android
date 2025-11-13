package com.unovil.tardyscan.core.model

data class AllowedUser (
    val domain: String,
    val domainId: String,
    val givenPassword: String
)