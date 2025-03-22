package com.unovil.tardyscan.data.repository

import com.unovil.tardyscan.domain.model.AllowedUser

interface AuthenticationRepository {
    enum class AllowedUserReturnType {NOT_FOUND, NOT_REGISTERED, ALREADY_REGISTERED, ERROR}

    suspend fun getAllowedUser(allowedUser: AllowedUser): AllowedUserReturnType
    suspend fun signUp(allowedUser: AllowedUser, email: String, password: String): Boolean
    suspend fun signIn(email: String, password: String): Boolean
}