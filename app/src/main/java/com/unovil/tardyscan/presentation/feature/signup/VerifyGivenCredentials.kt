package com.unovil.tardyscan.presentation.feature.signup

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unovil.tardyscan.presentation.common.AuthorizeButton
import com.unovil.tardyscan.presentation.common.PasswordTextField

@Composable
fun VerifyGivenCredentials(
    viewModel: SignUpViewModel? = hiltViewModel(),
    onSuccess: () -> Unit = { }
) {
    val domain = viewModel?.domain?.collectAsState()
    val domainId = viewModel?.domainId?.collectAsState()
    val rawPassword = viewModel?.rawPassword?.collectAsState()
    val verificationErrorMessage = viewModel?.verificationErrorMessage?.collectAsState()
    val isVerified = viewModel?.isVerified?.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(isVerified?.value) {
        if (isVerified?.value == true) onSuccess()
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
                    value = domain?.value ?: "",
                    onValueChange = { viewModel?.onDomainChange(it) },
                    label = { Text("Domain") },
                    placeholder = { Text("Enter domain") }
                )

                OutlinedTextField(
                    value = domainId?.value ?: "",
                    onValueChange = { viewModel?.onDomainIdChange(it) },
                    label = { Text("Domain ID") },
                    placeholder = { Text("Enter domain ID") }
                )

                PasswordTextField(
                    value = rawPassword?.value ?: "",
                    onValueChange = { viewModel?.onPasswordChange(it) }
                )

                Text(
                    text = verificationErrorMessage?.value ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )

                AuthorizeButton("Verify Credentials") {
                    viewModel?.onVerifyCredentials()
                    Toast.makeText(context, "Credentials button pressed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@Composable
@Preview
fun PreviewVerifyGivenCredentials() {
    VerifyGivenCredentials(viewModel = null)
}