package com.unovil.tardyscan.data.repository.impl

import com.unovil.tardyscan.data.network.dto.AllowedUserDto
import com.unovil.tardyscan.data.repository.AuthenticationRepository
import com.unovil.tardyscan.data.repository.AuthenticationRepository.AllowedUserResult
import com.unovil.tardyscan.data.repository.AuthenticationRepository.SignUpResult
import com.unovil.tardyscan.domain.model.AllowedUser
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.exception.AuthWeakPasswordException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth
) : AuthenticationRepository {

    override suspend fun getAllowedUser(allowedUser: AllowedUser): AllowedUserResult {
        val allowedUserDto = allowedUser.let { AllowedUserDto(it.domain, it.domainId, it.givenPassword) }

        val functionCall = postgrest.rpc(
            function = "verify_allowed_user",
            parameters = Json.encodeToJsonElement(AllowedUserDto.serializer(), allowedUserDto) as JsonObject
        ).decodeAs<String>()

        return when (functionCall) {
            "not_registered" -> AllowedUserResult.NOT_REGISTERED
            "already_registered" -> AllowedUserResult.ALREADY_REGISTERED
            "not_found" -> AllowedUserResult.NOT_FOUND
            else -> AllowedUserResult.ERROR
        }
    }

    override suspend fun signUp(
        allowedUser: AllowedUser,
        newEmail: String,
        newPassword: String
    ): SignUpResult {
        if (getAllowedUser(allowedUser) != AllowedUserResult.NOT_REGISTERED) {
            return SignUpResult.Failure.Unverified
        }

        try {
            auth.signUpWith(Email) {
                email = newEmail
                password = newPassword
            }
        } catch (e: AuthWeakPasswordException) {
            return SignUpResult.Failure.WeakPassword(e.reasons)
        } catch (_: Exception) {
            return SignUpResult.Failure.Unknown
        }

        return SignUpResult.Success
    }

    override suspend fun signIn(email: String, password: String): Boolean {
        TODO("Not yet implemented")
    }
}