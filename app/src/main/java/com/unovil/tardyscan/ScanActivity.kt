package com.unovil.tardyscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.ExperimentalGetImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.unovil.tardyscan.screens.CameraPermissionScreen
import com.unovil.tardyscan.screens.ScanningScreen
import com.unovil.tardyscan.ui.theme.TardyScannerTheme
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

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
                    ScanningScreen(
                        cameraExecutor,
                        onBack = { this.finish() }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}