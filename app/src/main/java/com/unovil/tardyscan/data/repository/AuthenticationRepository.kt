package com.unovil.tardyscan.data.repository

import com.unovil.tardyscan.domain.model.AllowedUser

interface AuthenticationRepository {
    enum class AllowedUserResult { NOT_FOUND, NOT_REGISTERED, ALREADY_REGISTERED, ERROR }
    sealed class SignUpResult {
        object Success : SignUpResult()
        sealed class Failure : SignUpResult() {
            object Unverified : Failure()
            class WeakPassword(val reasons: List<String>) : Failure()
            object Unknown : Failure()
        }
    }

    suspend fun getAllowedUser(allowedUser: AllowedUser): AllowedUserResult
    suspend fun signUp(allowedUser: AllowedUser, email: String, password: String): SignUpResult
    suspend fun signIn(email: String, password: String): Boolean
}