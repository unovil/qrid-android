package com.unovil.tardyscan.data.repository.impl

import android.util.Log
import com.unovil.tardyscan.data.network.dto.AllowedUserDto
import com.unovil.tardyscan.data.network.dto.VerifyAllowedUserRpcDto
import com.unovil.tardyscan.data.repository.AuthenticationRepository
import com.unovil.tardyscan.data.repository.AuthenticationRepository.AllowedUserResult
import com.unovil.tardyscan.di.AuthNameManager
import com.unovil.tardyscan.domain.model.AllowedUser
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SignOutScope
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
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
    private val auth: Auth,
    private val nameManager: AuthNameManager
) : AuthenticationRepository {

    val allowedUsersTable = postgrest["allowed_users"]

    override suspend fun getAllowedUserResult(allowedUser: AllowedUser): AllowedUserResult {
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

    override suspend fun updateAllowedUser() {
        val allowedUser = allowedUsersTable.select(Columns.list("id, domain, org_id, name, role")) {
            limit(1)
            single()
        }.decodeAs<AllowedUserDto>()

        nameManager.setAllowedUser(allowedUser)
        nameManager.setAllowedUserName(allowedUser.name ?: "")
    }

    override suspend fun signUp(
        allowedUser: AllowedUser,
        newEmail: String,
        newPassword: String
    ) {
        val allowedUserResult = getAllowedUserResult(allowedUser)
        if (allowedUserResult !is AllowedUserResult.Success) {
            throw IllegalAccessException("User is not allowed to sign up.")
        }

        Log.d("AuthenticationRepository", "allowedUserId: ${allowedUserResult.allowedUserId}")

        auth.signUpWith(Email) {
            email = newEmail
            password = newPassword
            data = buildJsonObject {
                put("allowed_user_id", JsonPrimitive(allowedUserResult.allowedUserId))
            }
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
        if (markRegisteredResult != 0) throw IllegalStateException("Failed to mark user as registered.")
    }

    override suspend fun signIn(enteredEmail: String, enteredPassword: String) {
        auth.signInWith(Email) {
            email = enteredEmail
            password = enteredPassword
        }
        auth.signOut(SignOutScope.OTHERS)

        val allowedUser = allowedUsersTable.select(Columns.list("id, domain, org_id, name, role")) {
            limit(1)
            single()
        }.decodeAs<AllowedUserDto>()

        nameManager.setAllowedUserName(allowedUser.name ?: "")
    }

    override suspend fun signOut() {
        auth.signOut(SignOutScope.LOCAL)
        nameManager.setAllowedUser(AllowedUserDto(0, "", "", "", ""))
        nameManager.setAllowedUserName("")
    }
}