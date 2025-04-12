package com.unovil.tardyscan.presentation.feature.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unovil.tardyscan.presentation.common.AuthorizeButton
import com.unovil.tardyscan.presentation.common.PasswordTextField

@Composable
fun SignUp(
    viewModel: SignUpViewModel? = hiltViewModel(),
    onSuccess: () -> Unit = { }
) {
    val newPassword = viewModel?.newPassword?.collectAsState()
    val newEmail = viewModel?.newEmail?.collectAsState()
    val signUpErrorMessage = viewModel?.signUpErrorMessage?.collectAsState()
    val isSuccessfulSignUp = viewModel?.isSuccessfulSignUp?.collectAsState()

    LaunchedEffect(isSuccessfulSignUp?.value) {
        if (isSuccessfulSignUp?.value == true) onSuccess()
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
                    value = newEmail?.value ?: "",
                    onValueChange = { viewModel?.onNewEmailChange(it) },
                    label = { Text("Email Address") },
                    placeholder = { Text("Enter your email address")}
                )

                PasswordTextField(
                    value = newPassword?.value ?: "",
                    onValueChange = { viewModel?.onNewPasswordChange(it) }
                )

                Text(
                    text = signUpErrorMessage?.value ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )

                AuthorizeButton("Sign up") { viewModel?.onSignUp() }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    SignUp(viewModel = null)
}
