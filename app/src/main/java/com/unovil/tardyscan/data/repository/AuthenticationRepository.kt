package com.unovil.tardyscan.data.repository

import com.unovil.tardyscan.domain.model.AdministratorUser

interface AuthenticationRepository {
    sealed class UserRpcResult {
        class Success(val allowedUserId: Int, val hashedPassword: String) : UserRpcResult()

        sealed class Failure : UserRpcResult() {
            object NotFound : Failure()
            object AlreadyRegistered : Failure()
            object Unknown : Failure()
        }
    }

    suspend fun getUserResult(administratorUser: AdministratorUser): UserRpcResult
    suspend fun updateAllowedUser()
    suspend fun signUp(administratorUser: AdministratorUser, email: String, password: String)
    suspend fun signIn(email: String, password: String)
    suspend fun signOut()
}