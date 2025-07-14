package com.unovil.tardyscan

/*
import android.content.Context
import android.util.Log
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.unovil.tardyscan.domain.usecase.GetStudentInfoUseCase
import com.unovil.tardyscan.domain.usecase.SignInUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.status.SessionStatus
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class SupabaseTest {
    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) val rule = createAndroidComposeRule<HiltTestActivity>()
    val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Inject lateinit var signInUseCase: SignInUseCase
    @Inject lateinit var getStudentInfoUseCase: GetStudentInfoUseCase
    @Inject lateinit var auth: Auth

    @Before
    fun setUp() {
        hiltRule.inject()
        runBlocking {
            signInUseCase.execute(SignInUseCase.Input(
                "uno070825@gmail.com",
                "Philippines +63"
            ))

            repeat(20) {
                if (auth.sessionStatus.value is SessionStatus.Authenticated) return@runBlocking
                delay(100)
            }

            check(auth.sessionStatus.value is SessionStatus.Authenticated) {
                "Failed to authenticate before starting the tests."
            }
        }
    }

    @Test
    fun inputValidStudentId_returnsStudentInfo() {
        assertTrue(auth.sessionStatus.value is SessionStatus.Authenticated)

        var result: GetStudentInfoUseCase.Output? = null

        runBlocking {
            result = getStudentInfoUseCase.execute(GetStudentInfoUseCase.Input(691865223147))

            repeat(20) {
                if (result is GetStudentInfoUseCase.Output.Success) return@runBlocking
                delay(100)
            }
        }

        Log.d("SupabaseTest", "Student info for valid: $result")
        assertTrue(result is GetStudentInfoUseCase.Output.Success)

    }

    @Test
    fun inputInvalidStudentId_returnsNotFound() {
        assertTrue(auth.sessionStatus.value is SessionStatus.Authenticated)

        var result: GetStudentInfoUseCase.Output? = null

        runBlocking {
            result = getStudentInfoUseCase.execute(GetStudentInfoUseCase.Input(121339023221))

            repeat(20) {
                if (result is GetStudentInfoUseCase.Output.Failure.NotFound) return@runBlocking
                delay(100)
            }
        }

        Log.d("SupabaseTest", "Student info for invalid: $result")
        assertTrue(result is GetStudentInfoUseCase.Output.Failure.NotFound)

    }
    
}*/
