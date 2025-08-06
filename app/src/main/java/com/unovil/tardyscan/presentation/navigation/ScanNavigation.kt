package com.unovil.tardyscan.presentation.navigation

import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.unovil.tardyscan.R
import com.unovil.tardyscan.presentation.feature.scan.CameraPermissionScreen
import com.unovil.tardyscan.presentation.feature.scan.ScanViewModel
import com.unovil.tardyscan.presentation.feature.scan.ScanningScreen
import com.unovil.tardyscan.presentation.feature.scan.SuccessfulScanCard
import java.util.concurrent.ExecutorService

@ExperimentalPermissionsApi
@ExperimentalGetImage
@Composable
fun ScanNavigation(cameraExecutor: ExecutorService, onBack: () -> Unit) {
    val context = LocalContext.current

    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBack()
            }
        }
    }

    val onScanSuccessSound = remember {
        SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
    }

    val onAttendanceAcceptSound = remember {
        SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
    }

    var onScanSuccessSoundId by remember { mutableIntStateOf(0) }
    var onAttendanceAcceptSoundId by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        onScanSuccessSoundId = onScanSuccessSound.load(context, R.raw.freesound_community_scan_success, 1)
        onAttendanceAcceptSoundId = onAttendanceAcceptSound.load(context, R.raw.chrisiex_attendance_approve, 1)
    }

    DisposableEffect(Unit) {
        (context as ComponentActivity)
            .onBackPressedDispatcher
            .addCallback(backCallback)

        onDispose {
            onScanSuccessSound.release()
            onAttendanceAcceptSound.release()
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
                    onBack = onBack,
                    onNavigate = {
                        Log.d("ScanNavigation", "onNavigate ran from ScanningScreen")
                        onScanSuccessSound.play(onScanSuccessSoundId, 1f, 1f, 0, 0, 1f)
                        navController.navigate(Screen.SuccessfulScan)
                    }
                )
            }

            dialog<Screen.SuccessfulScan>(
                dialogProperties = DialogProperties(
                    dismissOnClickOutside = false,
                    dismissOnBackPress = false,
                )
            ) {
                val onNavigate: () -> Unit = {
                    scanViewModel.resetNavigationFlag()
                    navController.popBackStack()
                }
                SuccessfulScanCard(
                    viewModel = scanViewModel,
                    onNavigate = onNavigate,
                    onSubmit = {
                        scanViewModel.onSubmitAttendance (
                            {
                                Log.d("ScanNavigation", "onNavigate ran from SuccessfulScanCard")
                                onAttendanceAcceptSound.play(onAttendanceAcceptSoundId, 1f, 1f, 0, 0, 1f)
                                Toast.makeText(context, "Submitted attendance!", Toast.LENGTH_SHORT).show()
                                onNavigate()
                                scanViewModel.onReset()
                            },
                            {
                                Log.d("ScanNavigation", "onNavigate ran from SuccessfulScanCard")
                                onAttendanceAcceptSound.play(onAttendanceAcceptSoundId, 1f, 1f, 0, 0, 1f)
                                Toast.makeText(context, "Duplicate attendance!", Toast.LENGTH_SHORT).show()
                                onNavigate()
                                scanViewModel.onReset()
                            },
                            {
                                Log.d("ScanNavigation", "onNavigate ran from SuccessfulScanCard (fail)")
                                Toast.makeText(context, "Please check your Internet connection and try again.", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                )
            }
        }
    }
}