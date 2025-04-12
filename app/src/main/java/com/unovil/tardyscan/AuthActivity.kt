package com.unovil.tardyscan

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unovil.tardyscan.presentation.feature.signin.SignIn
import com.unovil.tardyscan.presentation.feature.signup.SignUp
import com.unovil.tardyscan.presentation.feature.signup.SignUpViewModel
import com.unovil.tardyscan.presentation.feature.signup.VerifyGivenCredentials
import com.unovil.tardyscan.presentation.navigation.Screens
import com.unovil.tardyscan.ui.theme.TardyScannerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TardyScannerTheme {
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
                                onSuccess = {
                                    this@AuthActivity.startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                                    finish()
                                },
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
                                onSuccess = {
                                    this@AuthActivity.startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                                    finish()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}