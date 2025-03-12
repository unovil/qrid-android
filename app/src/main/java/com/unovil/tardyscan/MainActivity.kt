package com.unovil.tardyscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unovil.tardyscan.screens.HistoryScreen
import com.unovil.tardyscan.screens.ScanScreen
import com.unovil.tardyscan.screens.SettingsScreen
import com.unovil.tardyscan.ui.theme.TardyScannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            TardyScannerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavigationBar(navController) }
                ) { paddingValues ->
                    NavHost (
                        navController = navController,
                        startDestination = Screens.Scan.route,
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
                        composable(Screens.Scan.route) {
                            ScanScreen(navController)
                        }
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