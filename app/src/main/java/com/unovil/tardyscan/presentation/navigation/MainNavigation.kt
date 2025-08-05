package com.unovil.tardyscan.presentation.navigation

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unovil.tardyscan.presentation.feature.history.HistoryScreen
import com.unovil.tardyscan.presentation.feature.history.HistoryViewModel
import com.unovil.tardyscan.presentation.feature.settings.SettingsScreen
import com.unovil.tardyscan.presentation.feature.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(
    authName: String?,
    onScan: () -> Unit
) {
    val navController = rememberNavController()
    val historyViewModel: HistoryViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavigationBarMain(navController) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onScan
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
            startDestination = Screen.History.routeName,
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

            composable(Screen.History.routeName) {
                HistoryScreen(historyViewModel)
            }
            composable(Screen.Settings.routeName) {
                SettingsScreen(settingsViewModel, authName = authName)
            }
        }
    }
}
