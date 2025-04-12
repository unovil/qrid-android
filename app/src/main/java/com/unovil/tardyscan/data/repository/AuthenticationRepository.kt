package com.unovil.tardyscan.data.repository

import com.unovil.tardyscan.domain.model.AllowedUser

interface AuthenticationRepository {
    sealed class AllowedUserResult {
        class Success(val allowedUserId: Int) : AllowedUserResult()

        sealed class Failure : AllowedUserResult() {
            object NotFound : Failure()
            object AlreadyRegistered : Failure()
            object Unknown : Failure()
        }
    }


    sealed class SignUpResult {
        object Success : SignUpResult()
        sealed class Failure : SignUpResult() {
            object Unverified : Failure()
            class WeakPassword(val reasons: List<String>) : Failure()
            object AlreadyExists : Failure()
            object Unknown : Failure()
        }
    }
    sealed class SignInResult {
        object Success : SignInResult()
        sealed class Failure : SignInResult() {
            object HttpTimeout : Failure()
            object HttpNetworkError : Failure()
            object InvalidCredentials : Failure()
            object Unknown : Failure()
        }
    }

    suspend fun getAllowedUser(allowedUser: AllowedUser): AllowedUserResult
    suspend fun signUp(allowedUser: AllowedUser, email: String, password: String): SignUpResult
    suspend fun signIn(email: String, password: String): SignInResult
}