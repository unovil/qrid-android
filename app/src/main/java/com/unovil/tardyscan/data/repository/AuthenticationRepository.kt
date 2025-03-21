package com.unovil.tardyscan.data.repository

import com.unovil.tardyscan.domain.model.AllowedUser

interface AuthenticationRepository {
    suspend fun getAllowedUser(allowedUser: AllowedUser): Boolean
    suspend fun signUp(allowedUser: AllowedUser, email: String, password: String): Boolean
    suspend fun signIn(email: String, password: String): Boolean
}