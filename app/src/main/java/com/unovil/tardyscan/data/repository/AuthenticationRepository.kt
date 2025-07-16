package com.unovil.tardyscan.data.repository

import com.unovil.tardyscan.domain.model.AllowedUser

interface AuthenticationRepository {
    sealed class AllowedUserResult {
        class Success(val allowedUserId: Int, val hashedPassword: String) : AllowedUserResult()

        sealed class Failure : AllowedUserResult() {
            object NotFound : Failure()
            object AlreadyRegistered : Failure()
            object Unknown : Failure()
        }
    }

    suspend fun getAllowedUser(allowedUser: AllowedUser): AllowedUserResult
    suspend fun signUp(allowedUser: AllowedUser, email: String, password: String)
    suspend fun signIn(email: String, password: String)
    suspend fun signOut()
}