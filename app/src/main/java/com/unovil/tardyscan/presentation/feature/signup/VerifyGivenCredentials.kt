package com.unovil.tardyscan.presentation.feature.signup

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unovil.tardyscan.R
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
    onDomainChange: (String) -> Unit = { viewModel?.onDomainChange(it) },
    onDomainIdChange: (String) -> Unit = { viewModel?.onDomainIdChange(it) },
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

        OutlinedTextField(
            value = domain.value,
            onValueChange = onDomainChange,
            label = { Text(stringResource(R.string.domain)) },
            placeholder = { Text(stringResource(R.string.domain)) }
        )

        OutlinedTextField(
            value = domainId.value,
            onValueChange = onDomainIdChange,
            label = { Text(stringResource(R.string.domain_id)) },
            placeholder = { Text(stringResource(R.string.domain_id)) }
        )

        PasswordTextField(
            value = rawPassword.value,
            onValueChange = onPasswordChange
        )

        Text(
            text = verificationErrorMessage.value,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )

        AuthorizeButton(
            (domain.value.isNotBlank() && domainId.value.isNotBlank() && rawPassword.value.isNotBlank()),
            stringResource(R.string.verify_credentials)
        ) {
            onVerifyCredentials()
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewVerifyGivenCredentials() {
    val domain = remember { mutableStateOf("") }
    val domainId = remember { mutableStateOf("") }
    val rawPassword = remember { mutableStateOf("") }
    val verificationErrorMessage = remember { mutableStateOf("") }
    val isVerified = remember { mutableStateOf(false) }

    VerifyGivenCredentials(
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