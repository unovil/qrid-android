package com.unovil.tardyscan.screens

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.unovil.tardyscan.dialogs.CameraRequiredDialog
import com.unovil.tardyscan.ui.theme.TardyScannerTheme

@Composable
@Preview
fun ScanScreen(navController: NavController = rememberNavController()) {
    val context = LocalContext.current
    var hasCameraPermission by remember { mutableStateOf(false) }
    var showPermissionRationale by remember { mutableStateOf(false) }

    // launcher init to get permissions
    val permissionLauncher = rememberLauncherForActivityResult(
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
        if (!requestCameraPermission || hasCameraPermission) return@LaunchedEffect

        Log.d("ScanScreen", "Launching camera permission launcher...")
        permissionLauncher.launch(Manifest.permission.CAMERA)
        requestCameraPermission = false
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

    TardyScannerTheme {
        if (!hasCameraPermission) {
            AllowCameraScreen { requestCameraPermission = true }
        } else {
            val qrLauncher = rememberLauncherForActivityResult(
                ScanContract()
            ) {
                if (it.contents == null) {
                    Toast.makeText(context, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Scanned: ${it.contents}", Toast.LENGTH_LONG).show()
                }
            }

            val scanOptions = ScanOptions()
                .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                .setPrompt("Scan QR code")
                .setBeepEnabled(false)
                .setCameraId(0)
                .setBarcodeImageEnabled(true)

            LaunchedEffect(key1 = hasCameraPermission) {
                if (!hasCameraPermission) return@LaunchedEffect
                qrLauncher.launch(scanOptions)
            }

            /*var scanFlag by remember { mutableStateOf(false) }

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
            )*/
        }

    }
}

@Preview
@Composable
fun AllowCameraScreen(onClick: () -> Unit = { }) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column (
            modifier = Modifier.fillMaxSize().padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Camera permissions needed",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(20.dp)
            )
            Text(
                "QR-ID requires camera permissions to properly scan QR codes." +
                        "You can grant them by clicking the button below.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 40.dp)
            )
            Button (
                onClick = onClick,
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Text("Grant permission")
            }
        }
    }
}
