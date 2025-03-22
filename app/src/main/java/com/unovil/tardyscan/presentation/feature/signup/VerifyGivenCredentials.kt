package com.unovil.tardyscan.presentation.feature.signup

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
@Preview
fun VerifyGivenCredentials(
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val domain = viewModel.domain.collectAsState()
    val domainId = viewModel.domainId.collectAsState()
    val rawPassword = viewModel.rawPassword.collectAsState()
    val verificationErrorMessage = viewModel.verificationErrorMessage.collectAsState()
    val isVerified = viewModel.isVerified.collectAsState()

    LaunchedEffect(isVerified) {
        if (isVerified.value) {

        }
    }

    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
                    value = domain.value,
                    onValueChange = { viewModel.onDomainChange(it) },
                    label = { Text("Domain") },
                    placeholder = { Text("Enter domain") }
                )

                OutlinedTextField(
                    value = domainId.value,
                    onValueChange = { viewModel.onDomainIdChange(it) },
                    label = { Text("Domain ID") },
                    placeholder = { Text("Enter domain ID") }
                )

                OutlinedTextField(
                    value = rawPassword.value,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Password") },
                    placeholder = { Text("Enter password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription =
                                if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    }
                )

                Button(
                    modifier = Modifier.padding(top = 20.dp),
                    onClick = {
                        viewModel.onVerifyCredentials()
                        Toast.makeText(context, "Credentials button pressed", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Verify Credentials")
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Verify credentials"
                    )
                }
            }
            // TODO(): Provide a snackbar for when the credentials cannot be verified
        }
    }
}