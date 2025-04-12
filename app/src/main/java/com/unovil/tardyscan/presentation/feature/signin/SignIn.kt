package com.unovil.tardyscan.presentation.feature.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unovil.tardyscan.presentation.common.AuthorizeButton
import com.unovil.tardyscan.presentation.common.PasswordTextField

@Composable
fun SignIn(
    viewModel: SignInViewModel? = hiltViewModel(),
    onSuccess: () -> Unit,
    onSwitchToSignUp: () -> Unit
) {
    val email = viewModel?.email?.collectAsState()
    val password = viewModel?.password?.collectAsState()
    val signInErrorMessage = viewModel?.signInErrorMessage?.collectAsState()
    val isSuccessfulSignIn = viewModel?.isSuccessfulSignIn?.collectAsState()

    LaunchedEffect(isSuccessfulSignIn?.value) {
        if (isSuccessfulSignIn?.value == true) onSuccess()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = email?.value ?: "",
                    onValueChange = { viewModel?.onEmailChange(it) },
                    label = { Text("Email Address") },
                    placeholder = { Text("Enter your email address") }
                )

                PasswordTextField(
                    value = password?.value ?: "",
                    onValueChange = { viewModel?.onPasswordChange(it) }
                )

                Text(
                    text = signInErrorMessage?.value ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )

                AuthorizeButton(buttonText = "Sign in") { viewModel?.onSignIn() }

                TextButton(onClick = onSwitchToSignUp) {
                    Text(
                        text = "Don't have an account? Sign up here.",
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
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