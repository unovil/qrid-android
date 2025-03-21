package com.unovil.tardyscan.data.repository.impl

import com.unovil.tardyscan.data.network.dto.AllowedUserDto
import com.unovil.tardyscan.data.repository.AuthenticationRepository
import com.unovil.tardyscan.domain.model.AllowedUser
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth
) : AuthenticationRepository {

    private val allowedUsers = postgrest["allowed_users"]

    override suspend fun getAllowedUser(allowedUser: AllowedUser): Boolean {
        val result = allowedUsers.select(Columns.list("id, is_registered")) {
            filter {
                AllowedUserDto::domain eq allowedUser.domain
                AllowedUserDto::domainId eq allowedUser.domainId
                AllowedUserDto::givenPassword eq allowedUser.givenPassword
            }
        }.decodeSingleOrNull<AllowedUserDto>()

        return result != null && !result.isRegistered!!
    }

    override suspend fun signUp(
        allowedUser: AllowedUser,
        email: String,
        password: String
    ): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun signIn(email: String, password: String): Boolean {
        TODO("Not yet implemented")
    }
}