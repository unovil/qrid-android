package com.unovil.tardyscan.data.repository.impl

import android.util.Log
import com.unovil.tardyscan.data.network.dto.AllowedUserDto
import com.unovil.tardyscan.data.network.dto.VerifyAllowedUserRpcDto
import com.unovil.tardyscan.data.repository.AuthenticationRepository
import com.unovil.tardyscan.data.repository.AuthenticationRepository.AllowedUserResult
import com.unovil.tardyscan.data.repository.AuthenticationRepository.SignUpResult
import com.unovil.tardyscan.domain.model.AllowedUser
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.exception.AuthRestException
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

    val allowedUsers = postgrest["allowed_users"]

    override suspend fun getAllowedUser(allowedUser: AllowedUser): AllowedUserResult {
        val allowedUserDto = allowedUser.let { VerifyAllowedUserRpcDto(it.domain, it.domainId, it.givenPassword) }

        val functionCall = postgrest.rpc(
            function = "verify_allowed_user",
            parameters = Json.encodeToJsonElement(VerifyAllowedUserRpcDto.serializer(), allowedUserDto) as JsonObject
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
        } catch (e: AuthRestException) {
            // user credentials already exist in auth so this is a duplicate error
            if (e.message == null) throw e
            if (!e.message!!.startsWith("user_already_exists")) throw e
            return SignUpResult.Failure.AlreadyExists
        } catch (e: Exception) {
            Log.w("AuthenticationRepository", "Unknown exception:\n" +
                    "${e.javaClass.name}: ${e.message}")
            return SignUpResult.Failure.Unknown
        }

        val allowedUser = allowedUsers.select {
            filter {
                and {
                    AllowedUserDto::domain eq allowedUser.domain
                    AllowedUserDto::domainId eq allowedUser.domainId
                    AllowedUserDto::givenPassword eq allowedUser.givenPassword
                }
            }
            limit(1)
        }.decodeSingleOrNull<AllowedUserDto>()

        if (allowedUser == null) return SignUpResult.Failure.Unknown

        allowedUsers.update( {
            AllowedUserDto::isRegistered setTo true
        } ) {
            filter {
                AllowedUserDto::id eq allowedUser.id
            }
        }

        return SignUpResult.Success
    }

    override suspend fun signIn(email: String, password: String): Boolean {
        TODO("Not yet implemented")
    }
}