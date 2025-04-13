package com.unovil.tardyscan.presentation.feature.signup

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.unovil.tardyscan.presentation.common.AuthorizeButton
import com.unovil.tardyscan.presentation.common.PasswordTextField

@Composable
fun VerifyGivenCredentials(
    viewModel: SignUpViewModel? = hiltViewModel(),
    domain: State<String> = viewModel!!.domain.collectAsState(),
    domainId: State<String> = viewModel!!.domainId.collectAsState(),
    rawPassword: State<String> = viewModel!!.rawPassword.collectAsState(),
    verificationErrorMessage: State<String> = viewModel!!.verificationErrorMessage.collectAsState(),
    isVerified: State<Boolean> = viewModel!!.isVerified.collectAsState(),
    onSuccess: () -> Unit = { }
) {
    LaunchedEffect(isVerified.value) {
        if (isVerified.value) onSuccess()
    }

    OutlinedTextField(
        value = domain.value,
        onValueChange = { viewModel?.onDomainChange(it) },
        label = { Text("Domain") },
        placeholder = { Text("Enter domain") }
    )

    OutlinedTextField(
        value = domainId.value,
        onValueChange = { viewModel?.onDomainIdChange(it) },
        label = { Text("Domain ID") },
        placeholder = { Text("Enter domain ID") }
    )

    PasswordTextField(
        value = rawPassword.value,
        onValueChange = { viewModel?.onPasswordChange(it) }
    )

    Text(
        text = verificationErrorMessage.value,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall
    )

    AuthorizeButton(
        (domain.value.isNotBlank() && domainId.value.isNotBlank() && rawPassword.value.isNotBlank()),
        "Verify Credentials"
    ) {
        viewModel?.onVerifyCredentials()
    }
}

@Composable
@Preview
fun PreviewVerifyGivenCredentials() {
    VerifyGivenCredentials(viewModel = null)
}