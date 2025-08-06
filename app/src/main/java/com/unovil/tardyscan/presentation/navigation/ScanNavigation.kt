package com.unovil.tardyscan.presentation.navigation

import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.unovil.tardyscan.presentation.feature.scan.CameraPermissionScreen
import com.unovil.tardyscan.presentation.feature.scan.ScanViewModel
import com.unovil.tardyscan.presentation.feature.scan.ScanningScreen
import com.unovil.tardyscan.presentation.feature.scan.SuccessfulScanCard
import java.util.concurrent.ExecutorService

@ExperimentalPermissionsApi
@ExperimentalGetImage
@Composable
fun ScanNavigation(cameraExecutor: ExecutorService, onBack: () -> Unit) {
    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBack()
            }
        }
    }

    val context = LocalContext.current

    DisposableEffect(Unit) {
        (context as ComponentActivity)
            .onBackPressedDispatcher
            .addCallback(backCallback)

        onDispose {
            backCallback.remove()
        }
    }

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    if (!cameraPermissionState.status.isGranted) {
        CameraPermissionScreen { cameraPermissionState.launchPermissionRequest() }
    } else {
        val navController = rememberNavController()
        val scanViewModel = hiltViewModel<ScanViewModel>()

        NavHost(navController, startDestination = Screen.Scanning) {
            composable<Screen.Scanning> {
                ScanningScreen(
                    viewModel = scanViewModel,
                    executor = cameraExecutor,
                    onNavigate = { navController.navigate(Screen.SuccessfulScan) },
                    onBack = onBack
                )
            }

            dialog<Screen.SuccessfulScan>(
                dialogProperties = DialogProperties(
                    dismissOnClickOutside = false,
                    dismissOnBackPress = false,
                )
            ) {
                SuccessfulScanCard(
                    viewModel = scanViewModel,
                    onNavigate = {
                        scanViewModel.resetNavigationFlag()
                        navController.popBackStack()
                                 },
                )
            }
        }
    }
}