package com.unovil.tardyscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                        startDestination = Screens.VerifyGivenCredentials.route,
                        Modifier.padding(innerPadding)
                    ) {
                        composable(Screens.VerifyGivenCredentials.route) {
                            VerifyGivenCredentials(navController = navController)
                        }
                        composable(Screens.SignUp.route) {
                            SignUp(navController = navController)
                        }
                    }
                }
            }
        }
    }
}