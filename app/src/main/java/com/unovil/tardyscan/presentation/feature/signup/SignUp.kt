package com.unovil.tardyscan.presentation.feature.signup

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unovil.tardyscan.domain.helpers.PasswordValidation
import com.unovil.tardyscan.presentation.common.AuthorizeButton
import com.unovil.tardyscan.presentation.common.PasswordStrengthIndicator
import com.unovil.tardyscan.presentation.common.PasswordTextField
import com.unovil.tardyscan.presentation.common.PasswordValidationFeedbackItem
import com.unovil.tardyscan.ui.theme.TardyScannerTheme

@Composable
fun SignUp(
    viewModel: SignUpViewModel? = hiltViewModel(),
    newPassword: State<String> = viewModel!!.newPassword.collectAsState(),
    newEmail: State<String> = viewModel!!.newEmail.collectAsState(),
    signUpErrorMessage: State<String> = viewModel!!.signUpErrorMessage.collectAsState(),
    isSuccessfulSignUp: State<Boolean> = viewModel!!.isSuccessfulSignUp.collectAsState(),
    passwordValidations: State<Map<String, Boolean>> = viewModel!!.passwordValidations.collectAsState(),
    passwordStrength: State<Int> = viewModel!!.passwordStrength.collectAsState(),
    isSignUpButtonEnabled: State<Boolean> = viewModel!!.isSignUpButtonEnabled.collectAsState(),
    onEmailChange: (String) -> Unit = { viewModel!!.onNewEmailChange(it) },
    onPasswordChange: (String) -> Unit = { viewModel!!.onNewPasswordChange(it) },
    onSignUpClick: () -> Unit = { viewModel!!.onSignUp() },
    onSuccess: () -> Unit = { }
) {
    LaunchedEffect(isSuccessfulSignUp.value) {
        if (isSuccessfulSignUp.value) onSuccess()
    }

    OutlinedTextField(
        value = newEmail.value,
        onValueChange = onEmailChange,
        isError = newEmail.value.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")),
        label = { Text("Email Address") },
        placeholder = { Text("Enter your email address")}
    )

    PasswordTextField(
        value = newPassword.value,
        onValueChange = onPasswordChange
    )
    PasswordStrengthIndicator(currentStrength = (passwordStrength.value.plus(1)))
    Column(Modifier.fillMaxWidth()) {
        for ((message, isRuleMet) in passwordValidations.value) {
            PasswordValidationFeedbackItem(message, isRuleMet)
        }
    }

    Text(
        text = signUpErrorMessage.value,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall
    )

    AuthorizeButton(
        isSignUpButtonEnabled.value,
        "Sign up"
    ) {
        onSignUpClick()
    }
}


@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SignUpPreview() {
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
                    "Must be at least 8 characters" to false,
                    "Must contain at least one lowercase letter" to false,
                    "Must contain at least one uppercase letter" to false,
                    "Must contain at least one number" to false,
                    "Must contain at least one special character" to false
                )) }
                val passwordStrength = remember { mutableIntStateOf(0) }
                val isSignUpButtonEnabled = remember { mutableStateOf(false) }

                SignUp(
                    null,
                    password,
                    email,
                    signUpErrorMessage,
                    isSuccessfulSignUp,
                    passwordValidations,
                    passwordStrength,
                    isSignUpButtonEnabled,
                    { email.value = it },
                    {
                        password.value = it
                        passwordValidations.value = mapOf(
                            "Must be at least 8 characters" to PasswordValidation.hasMinimumLength(it),
                            "Must contain at least one lowercase letter" to PasswordValidation.hasLowercase(it),
                            "Must contain at least one uppercase letter" to PasswordValidation.hasUppercase(it),
                            "Must contain at least one number" to PasswordValidation.hasNumber(it),
                            "Must contain at least one special character" to PasswordValidation.hasSpecialCharacter(it)
                        )
                        passwordStrength.intValue = passwordValidations.value.count { it.value }
                    },
                )
            }
        }
    }
}
