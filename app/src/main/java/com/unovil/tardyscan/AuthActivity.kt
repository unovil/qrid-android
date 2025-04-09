package com.unovil.tardyscan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.unovil.tardyscan.MainActivity
import com.unovil.tardyscan.presentation.feature.signup.SignUp
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

                Scaffold { innerPadding ->
                    NavHost(
                        navController,
                        startDestination = "auth_flow",
                        Modifier.padding(innerPadding)
                    ) {
                        navigation(
                            startDestination = Screens.VerifyGivenCredentials.route,
                            route = "auth_flow"
                        ) {
                            composable(Screens.VerifyGivenCredentials.route) {
                                val parentEntry = remember(navController.currentBackStackEntry) {
                                    navController.getBackStackEntry("auth_flow")
                                }

                                VerifyGivenCredentials(
                                    viewModel = hiltViewModel(parentEntry),
                                    onSuccess = { navController.navigate(Screens.SignUp.route) }
                                )
                            }
                            composable(Screens.SignUp.route) {
                                val parentEntry = remember(navController.currentBackStackEntry) {
                                    navController.getBackStackEntry("auth_flow")
                                }

                                SignUp(
                                    viewModel = hiltViewModel(parentEntry),
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
}