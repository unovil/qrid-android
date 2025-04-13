package com.unovil.tardyscan.presentation.feature.signin

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.unovil.tardyscan.presentation.common.AuthorizeButton
import com.unovil.tardyscan.presentation.common.PasswordTextField

@Composable
fun SignIn(
    viewModel: SignInViewModel? = hiltViewModel(),
    email: State<String> = viewModel!!.email.collectAsState(),
    password: State<String> = viewModel!!.password.collectAsState(),
    signInErrorMessage: State<String> = viewModel!!.signInErrorMessage.collectAsState(),
    isSuccessfulSignIn: State<Boolean> = viewModel!!.isSuccessfulSignIn.collectAsState(),
    onSuccess: () -> Unit,
    onSwitchToSignUp: () -> Unit
) {
    LaunchedEffect(isSuccessfulSignIn.value) {
        if (isSuccessfulSignIn.value) onSuccess()
    }

    OutlinedTextField(
        value = email.value,
        onValueChange = { viewModel?.onEmailChange(it) },
        label = { Text("Email Address") },
        placeholder = { Text("Enter your email address") }
    )

    PasswordTextField(
        value = password.value,
        onValueChange = { viewModel?.onPasswordChange(it) }
    )

    Text(
        text = signInErrorMessage.value,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall
    )

    AuthorizeButton(
        enabled = (password.value.isNotBlank() && email.value.isNotBlank()),
        buttonText = "Sign in"
    ) {
        viewModel?.onSignIn()
    }

    TextButton(onClick = onSwitchToSignUp) {
        Text(
            text = "Don't have an account? Sign up here.",
            textDecoration = TextDecoration.Underline
        )
    }
}

@Preview(name = "SignIn", showSystemUi = true)
@Composable
fun SignInPreview() {
    SignIn(
        viewModel = null,
        onSuccess = { },
        onSwitchToSignUp = { }
    )
}