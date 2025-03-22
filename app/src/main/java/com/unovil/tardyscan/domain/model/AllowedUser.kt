package com.unovil.tardyscan.domain.model

data class AllowedUser (
    val domain: String,
    val domainId: String,
    val givenPassword: String
)