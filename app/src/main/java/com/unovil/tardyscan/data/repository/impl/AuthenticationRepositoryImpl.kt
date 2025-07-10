package com.unovil.tardyscan.data.repository.impl

import android.util.Log
import com.unovil.tardyscan.data.network.dto.VerifyAllowedUserRpcDto
import com.unovil.tardyscan.data.repository.AuthenticationRepository
import com.unovil.tardyscan.data.repository.AuthenticationRepository.AllowedUserResult
import com.unovil.tardyscan.data.repository.AuthenticationRepository.SignInResult
import com.unovil.tardyscan.data.repository.AuthenticationRepository.SignOutResult
import com.unovil.tardyscan.data.repository.AuthenticationRepository.SignUpResult
import com.unovil.tardyscan.domain.model.AllowedUser
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SignOutScope
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.exception.AuthWeakPasswordException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth
) : AuthenticationRepository {

    override suspend fun getAllowedUser(allowedUser: AllowedUser): AllowedUserResult {
        val allowedUserDto = allowedUser.let { VerifyAllowedUserRpcDto(it.domain, it.domainId) }

        val functionCall = postgrest.rpc(
            function = "get_allowed_user_json",
            parameters = Json.encodeToJsonElement(VerifyAllowedUserRpcDto.serializer(), allowedUserDto) as JsonObject
        ).data
        
        val functionCallJson = Json.parseToJsonElement(functionCall).jsonObject
        val status = functionCallJson["status"]?.jsonPrimitive?.int
        val hashedPassword = functionCallJson["hashedPassword"]?.jsonPrimitive?.contentOrNull

        return when (status) {
            -1 -> AllowedUserResult.Failure.NotFound
            0 -> AllowedUserResult.Failure.AlreadyRegistered
            null -> AllowedUserResult.Failure.Unknown
            else -> AllowedUserResult.Success(status, hashedPassword!!)
        }
    }

    override suspend fun signUp(
        allowedUser: AllowedUser,
        newEmail: String,
        newPassword: String
    ): SignUpResult {
        val allowedUserResult = getAllowedUser(allowedUser)
        if (allowedUserResult !is AllowedUserResult.Success) {
            return SignUpResult.Failure.Unverified
        }

        Log.d("AuthenticationRepository", "allowedUserId: ${allowedUserResult.allowedUserId}")

        try {
            auth.signUpWith(Email) {
                email = newEmail
                password = newPassword
                data = buildJsonObject {
                    put("allowed_user_id", JsonPrimitive(allowedUserResult.allowedUserId))
                }
            }
        } catch (e: AuthWeakPasswordException) {
            return SignUpResult.Failure.WeakPassword(e.reasons)
        } catch (e: AuthRestException) {
            if (e.errorCode != AuthErrorCode.UserAlreadyExists) throw e
            return SignUpResult.Failure.AlreadyExists
        } catch (e: Exception) {
            Log.w("AuthenticationRepository", "Unknown exception:\n" +
                    "${e.javaClass.name}: ${e.message}")
            return SignUpResult.Failure.Unknown
        }

        val user = auth.currentUserOrNull()
        val allowedUserId = (user?.userMetadata?.get("allowed_user_id") as JsonPrimitive).content.toInt()

        val markRegisteredResult = postgrest.rpc(
            function = "mark_as_registered",
            parameters = buildJsonObject {
                put("allowed_id", JsonPrimitive(allowedUserId))
            }
        ).decodeAs<Int>()

        // registered result of 0: success, -1: failure
        if (markRegisteredResult != 0) return SignUpResult.Failure.Unknown

        return SignUpResult.Success
    }

    override suspend fun signIn(enteredEmail: String, enteredPassword: String): SignInResult {
        try {
            auth.signInWith(Email) {
                email = enteredEmail
                password = enteredPassword
            }
            auth.signOut(SignOutScope.OTHERS)
        } catch (_: HttpRequestTimeoutException) {
            return SignInResult.Failure.HttpTimeout
        } catch (_: HttpRequestException) {
            return SignInResult.Failure.HttpNetworkError
        } catch (e: AuthRestException) {
            Log.w("AuthenticationRepository", "AuthRestException:\n${e.errorCode}")
            if (e.errorCode != AuthErrorCode.InvalidCredentials) throw e
            return SignInResult.Failure.InvalidCredentials
        } catch (e: Exception) {
            Log.w("AuthenticationRepository", "Exception:\n${e.message}")
            return SignInResult.Failure.Unknown
        }

        return SignInResult.Success
    }

    override suspend fun signOut(): SignOutResult {
        try {
            auth.signOut(SignOutScope.LOCAL)
            return SignOutResult.Success
        } catch (e: Exception) {
            return when (e) {
                is AuthRestException -> SignOutResult.Failure.AuthError
                is HttpRequestException -> SignOutResult.Failure.HttpError
                is HttpRequestTimeoutException -> SignOutResult.Failure.HttpTimeout
                else -> SignOutResult.Failure.Unknown(e)
            }
        }
    }
}