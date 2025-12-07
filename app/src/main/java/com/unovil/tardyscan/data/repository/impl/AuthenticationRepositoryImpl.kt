package com.unovil.tardyscan.data.repository.impl

import android.util.Log
import com.unovil.tardyscan.data.network.dto.AdminUserDto
import com.unovil.tardyscan.data.network.dto.GetAdminUserRpcDto
import com.unovil.tardyscan.data.repository.AuthenticationRepository
import com.unovil.tardyscan.data.repository.AuthenticationRepository.UserRpcResult
import com.unovil.tardyscan.di.AuthNameManager
import com.unovil.tardyscan.domain.model.AdministratorUser
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

    val adminUsersTable = postgrest["admin_users"]

    override suspend fun getUserResult(administratorUser: AdministratorUser): UserRpcResult {
        val adminUserDto = administratorUser.let { GetAdminUserRpcDto(it.domain, it.domainId) }

        val functionCall = postgrest.rpc(
            function = "get_admin_user_json",
            parameters = Json.encodeToJsonElement(GetAdminUserRpcDto.serializer(), adminUserDto) as JsonObject
        ).data
        
        val functionCallJson = Json.parseToJsonElement(functionCall).jsonObject
        val status = functionCallJson["status"]?.jsonPrimitive?.int
        val hashedPassword = functionCallJson["hashedPassword"]?.jsonPrimitive?.contentOrNull

        return when (status) {
            -1 -> UserRpcResult.Failure.NotFound
            0 -> UserRpcResult.Failure.AlreadyRegistered
            null -> UserRpcResult.Failure.Unknown
            else -> UserRpcResult.Success(status, hashedPassword!!)
        }
    }

    override suspend fun updateAllowedUser() {
        val allowedUser = adminUsersTable.select(Columns.list("id, domain, org_id, name, role")) {
            limit(1)
            single()
        }.decodeAs<AdminUserDto>()

        nameManager.setAllowedUser(allowedUser)
        nameManager.setAllowedUserName(allowedUser.name ?: "")
    }

    override suspend fun signUp(
        administratorUser: AdministratorUser,
        email: String,
        password: String
    ) {
        val userResult = getUserResult(administratorUser)
        if (userResult !is UserRpcResult.Success) {
            throw IllegalAccessException("User is not allowed to sign up.")
        }

        Log.d("AuthenticationRepository", "adminUserId: ${userResult.allowedUserId}")

        auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = buildJsonObject {
                put("admin_user_id", JsonPrimitive(userResult.allowedUserId))
            }
        }

        val user = auth.currentUserOrNull()
        val adminUserId = (user?.userMetadata?.get("admin_user_id") as JsonPrimitive).content.toInt()

        val markRegisteredResult = postgrest.rpc(
            function = "admin_mark_as_registered",
            parameters = buildJsonObject {
                put("admin_id", JsonPrimitive(adminUserId))
            }
        ).decodeAs<Int>()

        // registered result of 0: success, -1: failure
        if (markRegisteredResult != 0) throw IllegalStateException("Failed to mark user as registered.")
    }

    override suspend fun signIn(email: String, password: String) {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        auth.signOut(SignOutScope.OTHERS)

        val allowedUser = adminUsersTable.select(Columns.list("id, domain, org_id, name, role")) {
            limit(1)
            single()
        }.decodeAs<AdminUserDto>()

        nameManager.setAllowedUserName(allowedUser.name ?: "")
    }

    override suspend fun signOut() {
        auth.signOut(SignOutScope.LOCAL)
        nameManager.setAllowedUser(AdminUserDto(0, "", "", "", ""))
        nameManager.setAllowedUserName("")
    }
}