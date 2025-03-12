package com.unovil.tardyscan.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.CompoundBarcodeView
import com.unovil.tardyscan.ui.theme.TardyScannerTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.unovil.tardyscan.dialogs.CameraRequiredDialog

@Composable
@Preview
fun ScanScreen(navController: NavController = rememberNavController()) {
    val context = LocalContext.current
    var hasCameraPermission by remember { mutableStateOf(false) }
    var showPermissionRationale by remember { mutableStateOf(false) }

    // launcher init to get permissions
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        Log.d("ScanScreen", "Camera permission granted: $isGranted")
        hasCameraPermission = isGranted
        if (!isGranted) showPermissionRationale = true
    }

    var requestCameraPermission by remember { mutableStateOf(true) }

    // launches permission launcher if needed
    LaunchedEffect(key1 = requestCameraPermission) {
        Log.d("ScanScreen", "Requesting camera permission: $requestCameraPermission")
        if (requestCameraPermission && !hasCameraPermission) {
            Log.d("ScanScreen", "Launching camera permission launcher...")
            launcher.launch(Manifest.permission.CAMERA)
            requestCameraPermission = false
        }
    }

    // rationale for permission of camera
    if (showPermissionRationale) {
        Log.d("ScanScreen", "Showing permission rationale dialog")
        CameraRequiredDialog(
            onDismiss = { showPermissionRationale = false },
            onConfirm = {
                requestCameraPermission = true
                showPermissionRationale = false
            }
        )
    }

    var scanFlag by remember { mutableStateOf(false) }

    val compoundQRView = remember {
        CompoundBarcodeView(context).apply {
            val capture = CaptureManager(context as Activity, this)
            capture.initializeFromIntent(context.intent, null)
            this.setStatusText("")
            capture.decode()
            this.decodeContinuous { result ->
                if (scanFlag) {
                    return@decodeContinuous
                }
                scanFlag = true
                result.text?.let { barCodeOrQr ->
                    // do something here!
                    scanFlag = false
                }

                // delay here!
            }
        }
    }

    AndroidView(
        modifier = Modifier,
        factory = { compoundQRView }
    )
}

