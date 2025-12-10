package com.unovil.tardyscan.presentation.feature.onboarding.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.unovil.tardyscan.R
import com.unovil.tardyscan.presentation.common.AuthorizeButton
import com.unovil.tardyscan.presentation.common.PasswordTextField
import com.unovil.tardyscan.ui.theme.TardyScannerTheme

@Composable
fun VerifyCredentialsStudent(
    viewModel: SignUpViewModel? = hiltViewModel(),
    lrn: State<String> = viewModel!!.lrn.collectAsState(),
    rawPassword: State<String> = viewModel!!.rawPassword.collectAsState(),
    verificationErrorMessage: State<String> = viewModel!!.verificationErrorMessage.collectAsState(),
    isVerified: State<Boolean> = viewModel!!.isVerified.collectAsState(),
    onLrnChange: (String) -> Unit = { viewModel?.onLrnChange(it) },
    onPasswordChange: (String) -> Unit = { viewModel?.onPasswordChange(it) },
    onVerifyCredentials: () -> Unit = { viewModel?.onVerifyCredentials() },
    onSuccess: () -> Unit
) {
    LaunchedEffect(isVerified.value) {
        if (isVerified.value) onSuccess()
    }

    Column(
        modifier = Modifier.width(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        Text(
            text = stringResource(R.string.signup_verify_credentials),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = lrn.value,
            onValueChange = onLrnChange,
            label = { Text(stringResource(R.string.lrn)) },
            placeholder = { Text(stringResource(R.string.lrn)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        PasswordTextField(
            value = rawPassword.value,
            onValueChange = onPasswordChange
        )

        Text(
            text = verificationErrorMessage.value,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )

        AuthorizeButton(
            (lrn.value.isNotBlank() && rawPassword.value.isNotBlank()),
            stringResource(R.string.verify_credentials)
        ) {
            onVerifyCredentials()
        }
    }
}

@Composable
@PreviewLightDark
private fun Preview() {
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
