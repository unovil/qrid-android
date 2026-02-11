package com.unovil.tardyscan.presentation.feature.onboarding.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.unovil.tardyscan.R
import com.unovil.tardyscan.domain.helpers.PasswordValidation
import com.unovil.tardyscan.ui.theme.TardyScannerTheme

@PreviewLightDark
@Composable
private fun SignUpPreview() {
    val context = LocalContext.current

    TardyScannerTheme {
        Surface {
            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                val password = remember { mutableStateOf("") }
                val email = remember { mutableStateOf("") }
                val signUpErrorMessage = remember { mutableStateOf("") }
                val isSuccessfulSignUp = remember { mutableStateOf(false) }
                val passwordValidations = remember { mutableStateOf(mapOf(
                    context.getString(R.string.password_requirement_length) to false,
                    context.getString(R.string.password_requirement_lowercase) to false,
                    context.getString(R.string.password_requirement_uppercase) to false,
                    context.getString(R.string.password_requirement_number) to false,
                    context.getString(R.string.password_requirement_special) to false
                )) }
                val isSignUpButtonEnabled = remember { mutableStateOf(false) }

                isSignUpButtonEnabled.value = email.value.matches(
                    Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
                ) && passwordValidations.value.values.count { it } == passwordValidations.value.size

                SignUp(
                    null,
                    password,
                    email,
                    signUpErrorMessage,
                    isSuccessfulSignUp,
                    passwordValidations,
                    isSignUpButtonEnabled,
                    { email.value = it },
                    {
                        password.value = it
                        passwordValidations.value = mapOf(
                            context.getString(R.string.password_requirement_length) to PasswordValidation.hasMinimumLength(it),
                            context.getString(R.string.password_requirement_lowercase) to PasswordValidation.hasLowercase(it),
                            context.getString(R.string.password_requirement_uppercase) to PasswordValidation.hasUppercase(it),
                            context.getString(R.string.password_requirement_number) to PasswordValidation.hasNumber(it),
                            context.getString(R.string.password_requirement_special) to PasswordValidation.hasSpecialCharacter(it)
                        )
                    },
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun VerifyCredentialsAdminPreview() {
    TardyScannerTheme {
        Surface {
            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val domain = remember { mutableStateOf("") }
                val domainId = remember { mutableStateOf("") }
                val rawPassword = remember { mutableStateOf("") }
                val verificationErrorMessage = remember { mutableStateOf("") }
                val isVerified = remember { mutableStateOf(false) }

                VerifyCredentialsAdmin(
                    viewModel = null,
                    domain,
                    domainId,
                    rawPassword,
                    verificationErrorMessage,
                    isVerified,
                    { domain.value = it },
                    { domainId.value = it },
                    { rawPassword.value = it },
                    { },
                    { }
                )
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun VerifyCredentialsStudentPreview() {
    TardyScannerTheme {
        Surface {
            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val lrn = remember { mutableStateOf("") }
                val rawPassword = remember { mutableStateOf("") }
                val verificationErrorMessage = remember { mutableStateOf("") }
                val isVerified = remember { mutableStateOf(false) }

                VerifyCredentialsStudent(
                    viewModel = null,
                    lrn,
                    rawPassword,
                    verificationErrorMessage,
                    isVerified,
                    { lrn.value = it },
                    { rawPassword.value = it },
                    { },
                    { }
                )
            }
        }
    }
}
