package com.unovil.tardyscan

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import com.unovil.tardyscan.presentation.feature.signup.VerifyGivenCredentials
import com.unovil.tardyscan.ui.theme.TardyScannerTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class VerifyAllowedUserCredentialsTest {
    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) val rule = createAndroidComposeRule<HiltTestActivity>()
    val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    val domainBox = rule.onNodeWithText(context.getString(R.string.domain))
    val domainIdBox = rule.onNodeWithText(context.getString(R.string.domain_id))
    val passwordBox = rule.onNodeWithText(context.getString(R.string.password))
    val verifyButton = rule.onNodeWithText(context.getString(R.string.verify_credentials))

    fun ComposeContentTestRule.waitForRunningTasks(@StringRes resId: Int, timeoutMillis: Long = 10_000) {
        this.waitUntil(timeoutMillis) { // wait up to 10 seconds
            this.onAllNodesWithText(context.getString(resId))
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    var success = false

    @Before
    fun setUp() {
        hiltRule.inject()
        rule.setContent {
            TardyScannerTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        VerifyGivenCredentials { success = true }
                    }
                }
            }
        }
    }

    /**
     * Test: Entering correct given credentials should navigate to signup screen
     */
    @Test
    fun enterCorrectCredentials_navigatesToSignupScreen() {
        // action
        domainBox.performTextInput("org.com")
        domainIdBox.performTextInput("12345")
        passwordBox.performTextInput("something something")
        verifyButton.performClick()

        // wait for running tasks
        rule.waitUntil(5_000) { success == true }

        // result: should be true
        assertTrue(success)

        success = false
    }

    /**
     * Test: Entering incorrect given credentials should show error message
     */
    @Test
    fun enterUnknownValidationCredentials_showsErrorMessage() {
        // action
        domainBox.performTextInput("org.com")
        domainIdBox.performTextInput("12345")
        passwordBox.performTextInput("wrong password")
        verifyButton.performClick()

        // wait for running tasks
        rule.waitForRunningTasks(R.string.auth_no_valid_creds_found)

        // result: should be false
        rule.onNodeWithText(context.getString(R.string.auth_no_valid_creds_found))
            .assertExists()
        assertTrue(!success)

        success = false
    }

    /**
     * Test: Entering credentials that are already registered should show error message
     */
    @Test
    fun enterRegisteredValidationCredentials_showsErrorMessage() {
        // action
        domainBox.performTextInput("org.com")
        domainIdBox.performTextInput("already_allowed")
        passwordBox.performTextInput("something something")

        // wait for running tasks
        rule.waitForRunningTasks(R.string.auth_valid_creds_already_registered)

        // result: should be false
        rule.onNodeWithText(context.getString(R.string.auth_valid_creds_already_registered))
            .assertExists()
        assertTrue(!success)

        success = false
    }
}