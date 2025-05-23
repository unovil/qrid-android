package com.unovil.tardyscan.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unovil.tardyscan.presentation.feature.signin.SignIn
import com.unovil.tardyscan.presentation.feature.signup.SignUp
import com.unovil.tardyscan.presentation.feature.signup.SignUpViewModel
import com.unovil.tardyscan.presentation.feature.signup.VerifyGivenCredentials

@Composable
fun AuthNavigation(
    onSuccess: () -> Unit
) {
    val navController = rememberNavController()
    val signUpViewModel: SignUpViewModel = hiltViewModel()

    Scaffold { innerPadding ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                NavHost(
                    navController,
                    startDestination = Screen.SignIn,
                    Modifier.padding(innerPadding)
                ) {
                    composable<Screen.SignIn> {
                        SignIn(
                            onSuccess = onSuccess,
                            onSwitchToSignUp = { navController.navigate(Screen.VerifyGivenCredentials) }
                        )
                    }

                    composable<Screen.VerifyGivenCredentials> {
                        VerifyGivenCredentials(
                            viewModel = signUpViewModel,
                            onSuccess = { navController.navigate(Screen.SignUp) }
                        )
                    }

                    composable<Screen.SignUp> {
                        SignUp(
                            viewModel = signUpViewModel,
                            onSuccess = onSuccess
                        )
                    }
                }
            }
        }


    }
}
