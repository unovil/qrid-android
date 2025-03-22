package com.unovil.tardyscan.data.repository.impl

import android.util.Log
import com.unovil.tardyscan.data.network.dto.AllowedUserDto
import com.unovil.tardyscan.data.repository.AuthenticationRepository
import com.unovil.tardyscan.data.repository.AuthenticationRepository.AllowedUserReturnType
import com.unovil.tardyscan.domain.model.AllowedUser
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth
) : AuthenticationRepository {

    private val allowedUsers = postgrest["allowed_users"]

    override suspend fun getAllowedUser(allowedUser: AllowedUser): AllowedUserReturnType {
        val allowedUserDto = allowedUser.let { AllowedUserDto(it.domain, it.domainId, it.givenPassword) }

        val functionCall = postgrest.rpc(
            function = "verify_allowed_user",
            parameters = Json.encodeToJsonElement(AllowedUserDto.serializer(), allowedUserDto) as JsonObject
        ).decodeAs<String>()

        return when (functionCall) {
            "not_registered" -> AllowedUserReturnType.NOT_REGISTERED
            "already_registered" -> AllowedUserReturnType.ALREADY_REGISTERED
            "not_found" -> AllowedUserReturnType.NOT_FOUND
            else -> AllowedUserReturnType.ERROR
        }
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