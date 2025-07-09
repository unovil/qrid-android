package com.unovil.tardyscan.presentation.navigation

import androidx.camera.core.ExperimentalGetImage
import androidx.compose.runtime.Composable
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

            dialog<Screen.SuccessfulScan> {
                SuccessfulScanCard(
                    viewModel = scanViewModel,
                    onNavigate = { navController.popBackStack() },
                )
            }
        }
    }
}