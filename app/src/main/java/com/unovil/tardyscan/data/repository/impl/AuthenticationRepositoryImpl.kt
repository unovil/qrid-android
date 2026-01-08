package com.unovil.tardyscan.data.repository.impl

import android.util.Log
import com.unovil.tardyscan.data.network.dto.AdminUserDto
import com.unovil.tardyscan.data.network.dto.GetAdminUserRpcDto
import com.unovil.tardyscan.data.network.dto.GetStudentUserRpcDto
import com.unovil.tardyscan.data.network.dto.StudentUserDto
import com.unovil.tardyscan.data.repository.AttendanceRepository
import com.unovil.tardyscan.data.repository.AuthenticationRepository
import com.unovil.tardyscan.data.repository.AuthenticationRepository.UserRpcResult
import com.unovil.tardyscan.di.AuthNameManager
import com.unovil.tardyscan.domain.model.Administrator
import com.unovil.tardyscan.domain.model.SignUpAdministratorUser
import com.unovil.tardyscan.domain.model.SignUpStudentUser
import com.unovil.tardyscan.domain.model.Student
import com.unovil.tardyscan.domain.model.User
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
    private val nameManager: AuthNameManager,
    private val attendanceRepository: AttendanceRepository
) : AuthenticationRepository {

    val adminUsersTable = postgrest["admin_users"]
    val studentUsersTable = postgrest["student_users"]

    override suspend fun getUserResult(signUpAdministratorUser: SignUpAdministratorUser): UserRpcResult {
        val adminUserDto = signUpAdministratorUser.let { GetAdminUserRpcDto(it.domain, it.domainId) }

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

    override suspend fun getUserResult(signUpStudentUser: SignUpStudentUser): UserRpcResult {
        val studentUserDto = GetStudentUserRpcDto(signUpStudentUser.lrn)

        val functionCall = postgrest.rpc(
            function = "get_student_user_json",
            parameters = Json.encodeToJsonElement(GetStudentUserRpcDto.serializer(), studentUserDto) as JsonObject
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
        val user = auth.retrieveUserForCurrentSession(true)
        if (user.userMetadata?.get("admin_user_id") != null) {
            val adminResponse = adminUsersTable.select(Columns.list("id, domain, org_id, name, role")) {
                limit(1)
                single()
            }.decodeAs<AdminUserDto>()
            
            val allowedUser = User.Administrator(Administrator(
                adminResponse.domain,
                adminResponse.domainId,
                adminResponse.name,
                adminResponse.role
            ))

            nameManager.setAllowedUser(allowedUser)
            nameManager.setAllowedUserName(allowedUser.admin.name ?: "")
        } else if (user.userMetadata?.get("student_user_id") != null) {
            val studentResponse = studentUsersTable.select(Columns.list("id, lrn")) {
                limit(1)
                single()
            }.decodeAs<StudentUserDto>().let { attendanceRepository.getStudentInfo(it.lrn) }

            val name = "${studentResponse?.lastName}, ${studentResponse?.firstName} ${studentResponse?.middleName}"

            val allowedUser = User.Student(Student(
                studentResponse?.id ?: 100000000000,
                studentResponse?.lastName ?: "",
                studentResponse?.firstName ?: "",
                studentResponse?.middleName,
                studentResponse?.section?.level ?: 0,
                studentResponse?.section?.section ?: "",
                studentResponse?.section?.school?.name ?: "",
                attendanceRepository.getAvatarFlow(studentResponse?.avatarLink ?: "")
            ))

            nameManager.setAllowedUser(allowedUser)
            nameManager.setAllowedUserName(name)
        }
    }

    override suspend fun signUp(
        signUpAdministratorUser: SignUpAdministratorUser,
        email: String,
        password: String
    ) {
        val userResult = getUserResult(signUpAdministratorUser)
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

    override suspend fun signUp(signUpStudentUser: SignUpStudentUser, email: String, password: String) {
        val userResult = getUserResult(signUpStudentUser)
        if (userResult !is UserRpcResult.Success) {
            throw IllegalAccessException("User is not allowed to sign up.")
        }

        Log.d("AuthenticationRepository", "studentUserId: ${userResult.allowedUserId}")

        auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = buildJsonObject {
                put("student_user_id", JsonPrimitive(userResult.allowedUserId))
            }
        }

        val user = auth.currentUserOrNull()
        val studentUserId = (user?.userMetadata?.get("student_user_id") as JsonPrimitive).content.toInt()

        val markRegisteredResult = postgrest.rpc(
            function = "student_mark_as_registered",
            parameters = buildJsonObject {
                put("student_id", JsonPrimitive(studentUserId))
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

        val userMetadata = auth.currentUserOrNull()?.userMetadata
        if (userMetadata?.get("allowed_user_id") != null) {
            val allowedUser = adminUsersTable.select(Columns.list("id, domain, org_id, name, role")) {
                limit(1)
                single()
            }.decodeAs<AdminUserDto>()

            nameManager.setAllowedUserName(allowedUser.name ?: "")
        } else if (userMetadata?.get("student_user_id") != null) {
            val studentUser = studentUsersTable.select(Columns.list("id, lrn")) {
                limit(1)
                single()
            }.decodeAs<StudentUserDto>()

            val student = attendanceRepository.getStudentInfo(studentUser.lrn)

            nameManager.setAllowedUserName(student?.firstName ?: "")
        }


    }

    override suspend fun signOut() {
        auth.signOut(SignOutScope.LOCAL)
        nameManager.setAllowedUser(null)
        nameManager.setAllowedUserName("")
    }
}