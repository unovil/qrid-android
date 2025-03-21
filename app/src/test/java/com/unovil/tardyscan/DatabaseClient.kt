package com.unovil.tardyscan

import com.unovil.tardyscan.data.network.dto.AllowedUserDto
import com.unovil.tardyscan.domain.model.AllowedUser
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns

class DatabaseClient(private val client: SupabaseClient){

    suspend fun getAllowedUsers(allowedUsers: List<AllowedUserDto>): List<AllowedUser> {
        return client
            .postgrest["allowed_users"]
            .select(Columns.list("id, is_registered")) {
                filter {

                }
            }
            .decodeList<AllowedUser>()
    }
}