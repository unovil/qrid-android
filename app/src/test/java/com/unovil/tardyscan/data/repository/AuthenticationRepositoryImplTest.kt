package com.unovil.tardyscan.data.repository

import com.unovil.tardyscan.data.network.dto.AllowedUserDto
import com.unovil.tardyscan.data.repository.impl.AuthenticationRepositoryImpl
import com.unovil.tardyscan.domain.model.AllowedUser
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.PostgrestQueryBuilder
import io.github.jan.supabase.postgrest.query.PostgrestRequestBuilder
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.BeforeTest
import kotlin.test.assertTrue

class AuthenticationRepositoryImplTest {

    private val postgrest = mockk<Postgrest>()
    private val mockAuth = mockk<Auth>()
    private val repository = AuthenticationRepositoryImpl(postgrest, mockAuth)

    @BeforeTest
    fun setUp() {
        // mocking request for postgrest["allowed_users"]
        val allowedUsersMock = mockk<PostgrestQueryBuilder>(relaxed = true)

        every { postgrest["allowed_users"] } returns allowedUsersMock
        coEvery { allowedUsersMock.select(any<Columns>()).decodeSingleOrNull<AllowedUserDto>() } returns
                AllowedUserDto(
                    domain = "org.com",
                    domainId = "12345",
                    givenPassword = "something something",
                    isRegistered = false
                )
    }

    @Test
    fun `should return true when allowedUser exists and is not registered`() = runTest {
        val allowedUser = AllowedUser(
            domain = "org.com",
            domainId = "12345",
            givenPassword = "something something"
        )

        val result = repository.getAllowedUser(allowedUser)
        assertTrue(result)
    }
}