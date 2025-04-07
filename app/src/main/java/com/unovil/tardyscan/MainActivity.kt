package com.unovil.tardyscan

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.unovil.tardyscan.presentation.feature.history.HistoryScreen
import com.unovil.tardyscan.presentation.feature.settings.SettingsScreen
import com.unovil.tardyscan.presentation.navigation.BottomNavigationBarMain
import com.unovil.tardyscan.presentation.navigation.Screens
import com.unovil.tardyscan.ui.theme.TardyScannerTheme
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var supabaseClient: SupabaseClient

    @ExperimentalPermissionsApi
    @ExperimentalGetImage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // test on authact first
        this.startActivity(Intent(this, AuthActivity::class.java))
        // supabaseClient.auth.retrieveUserForCurrentSession(updateSession = true)

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            TardyScannerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavigationBarMain(navController) },
                    floatingActionButton = {
                        ExtendedFloatingActionButton(
                            onClick = {
                                this.startActivity(Intent(this, ScanActivity::class.java))
                            }
                        ) {
                            Icon(Icons.Default.QrCodeScanner, "QR Scan Service")
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Scan")
                        }
                    },
                    floatingActionButtonPosition = FabPosition.Center
                ) { paddingValues ->
                    NavHost (
                        navController = navController,
                        startDestination = Screens.History.route,
                        enterTransition = { slideInHorizontally(
                            animationSpec = tween(500),
                            initialOffsetX = { it / 3 }
                        ) + fadeIn(animationSpec = tween(300)) },
                        exitTransition = { slideOutHorizontally(
                            animationSpec = tween(500),
                            targetOffsetX = { -it / 3 }
                        ) + fadeOut(animationSpec = tween(300)) },
                        modifier = Modifier.padding(paddingValues = paddingValues)
                    ) {
                        composable(Screens.History.route) {
                            HistoryScreen(navController)
                        }
                        composable(Screens.Settings.route) {
                            SettingsScreen(navController)
                        }
                    }
                }
            }
        }
    }
}