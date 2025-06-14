package com.unovil.tardyscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.ExperimentalGetImage
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
import com.unovil.tardyscan.presentation.navigation.Screen
import com.unovil.tardyscan.ui.theme.TardyScannerTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class ScanActivity : ComponentActivity() {
    private lateinit var cameraExecutor: ExecutorService

    @ExperimentalPermissionsApi
    @ExperimentalGetImage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        enableEdgeToEdge()
        setContent {
            TardyScannerTheme {
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
                                onBack = { this@ScanActivity.finish() },
                                onNavigate = { navController.navigate(Screen.SuccessfulScan) }
                            )
                        }

                        dialog<Screen.SuccessfulScan> {
                            SuccessfulScanCard(
                                viewModel = scanViewModel,
                                onNavigate = { navController.navigate(Screen.Scanning) },
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}