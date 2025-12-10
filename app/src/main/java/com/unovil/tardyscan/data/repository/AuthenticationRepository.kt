package com.unovil.tardyscan.data.repository

import com.unovil.tardyscan.domain.model.SignUpAdministratorUser
import com.unovil.tardyscan.domain.model.SignUpStudentUser

interface AuthenticationRepository {
    sealed class UserRpcResult {
        class Success(val allowedUserId: Int, val hashedPassword: String) : UserRpcResult()

        sealed class Failure : UserRpcResult() {
            object NotFound : Failure()
            object AlreadyRegistered : Failure()
            object Unknown : Failure()
        }
    }

    suspend fun getUserResult(signUpAdministratorUser: SignUpAdministratorUser): UserRpcResult
    suspend fun getUserResult(signUpStudentUser: SignUpStudentUser): UserRpcResult
    suspend fun updateAllowedUser()
    suspend fun signUp(signUpAdministratorUser: SignUpAdministratorUser, email: String, password: String)
    suspend fun signUp(signUpStudentUser: SignUpStudentUser, email: String, password: String)
    suspend fun signIn(email: String, password: String)
    suspend fun signOut()
}