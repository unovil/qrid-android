package com.unovil.tardyscan.presentation.feature.onboarding.signup

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.unovil.tardyscan.R
import com.unovil.tardyscan.domain.helpers.PasswordValidation
import com.unovil.tardyscan.presentation.common.AuthorizeButton
import com.unovil.tardyscan.presentation.common.PasswordStrengthIndicator
import com.unovil.tardyscan.presentation.common.PasswordTextField
import com.unovil.tardyscan.presentation.common.PasswordValidationFeedbackItem
import com.unovil.tardyscan.ui.theme.TardyScannerTheme
import kotlin.collections.iterator

@Composable
fun SignUp(
    viewModel: SignUpViewModel? = hiltViewModel(),
    newPassword: State<String> = viewModel!!.newPassword.collectAsState(),
    newEmail: State<String> = viewModel!!.newEmail.collectAsState(),
    signUpErrorMessage: State<String> = viewModel!!.signUpErrorMessage.collectAsState(),
    isSuccessfulSignUp: State<Boolean> = viewModel!!.isSuccessfulSignUp.collectAsState(),
    passwordValidations: State<Map<String, Boolean>> = viewModel!!.passwordValidations.collectAsState(),
    isSignUpButtonEnabled: State<Boolean> = viewModel!!.isSignUpButtonEnabled.collectAsState(),
    onEmailChange: (String) -> Unit = { viewModel!!.onNewEmailChange(it) },
    onPasswordChange: (String) -> Unit = { viewModel!!.onNewPasswordChange(it) },
    onSignUpClick: () -> Unit = { viewModel!!.onSignUp() },
    onSuccess: () -> Unit = { }
) {
    LaunchedEffect(isSuccessfulSignUp.value) {
        if (isSuccessfulSignUp.value) onSuccess()
    }

    Column(
        modifier = Modifier.width(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        Text(
            text = stringResource(R.string.sign_up),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = newEmail.value,
            onValueChange = onEmailChange,
            label = { Text(stringResource(R.string.email_address)) },
            placeholder = { Text(stringResource(R.string.email_address)) }
        )

        PasswordTextField(
            value = newPassword.value,
            onValueChange = onPasswordChange
        )
        PasswordStrengthIndicator(currentStrength = passwordValidations.value.values.count { it } + 1)
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
            stringResource(R.string.sign_up)
        ) {
            onSignUpClick()
        }
    }
}


@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SignUpPreview() {
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
