package com.unovil.tardyscan.presentation.feature.onboarding.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.unovil.tardyscan.R
import com.unovil.tardyscan.presentation.common.AuthorizeButton
import com.unovil.tardyscan.presentation.common.PasswordStrengthIndicator
import com.unovil.tardyscan.presentation.common.PasswordTextField
import com.unovil.tardyscan.presentation.common.PasswordValidationFeedbackItem

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