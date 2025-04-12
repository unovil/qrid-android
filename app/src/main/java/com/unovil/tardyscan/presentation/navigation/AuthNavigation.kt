package com.unovil.tardyscan.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
        NavHost(
            navController,
            startDestination = Screens.SignIn.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Screens.SignIn.route) {
                SignIn(
                    onSuccess = onSuccess,
                    onSwitchToSignUp = { navController.navigate(Screens.VerifyGivenCredentials.route) }
                )
            }

            composable(Screens.VerifyGivenCredentials.route) {
                VerifyGivenCredentials(
                    viewModel = signUpViewModel,
                    onSuccess = { navController.navigate(Screens.SignUp.route) }
                )
            }

            composable(Screens.SignUp.route) {
                SignUp(
                    viewModel = signUpViewModel,
                    onSuccess = onSuccess
                )
            }
        }
    }
}
