package com.unovil.tardyscan.presentation.feature.scan

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.unovil.tardyscan.domain.helpers.processImageProxy
import com.unovil.tardyscan.presentation.feature.scan.composables.SuccessfulScanCard
import java.util.concurrent.ExecutorService

@ExperimentalGetImage
@Composable
fun ScanningScreen(executor: ExecutorService, onBack: () -> Unit) {
    val context = LocalContext.current
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var scannedQrValue by remember { mutableStateOf<String?>(null) }
    var scanningEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(previewView, scanningEnabled) {
        previewView?.let { view ->
            scanningCoroutine(view, executor, context, scanningEnabled) {
                Log.d("Scan Screen", "Scanned! $it")
                scannedQrValue = it
                scanningEnabled = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView (
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                PreviewView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }.also { previewView = it }
            }
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val squareSize = 300.dp.toPx()
            val topLeft = center - Offset(squareSize / 2f, squareSize / 2f)

            // draw black background region
            drawPath(
                Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                    
                    moveTo(topLeft.x, topLeft.y)
                    lineTo(topLeft.x, topLeft.y + squareSize)
                    lineTo(topLeft.x + squareSize, topLeft.y + squareSize)
                    lineTo(topLeft.x + squareSize, topLeft.y)
                    close()
                },
                color = Color.Black.copy(alpha = 0.5f)
            )

            // draw white stroke
            drawRect(
                color = Color.White,
                topLeft = topLeft,
                size = Size(squareSize, squareSize),
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // back button
        Button(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding(),
            colors = ButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.White
            ),
            enabled = scanningEnabled,
            onClick = onBack
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
        }

        if (scannedQrValue != null && scannedQrValue!!.isNotEmpty()) {
            SuccessfulScanCard(
                scannedQrValue = scannedQrValue!!,
                onClick = {
                    scannedQrValue = null
                    scanningEnabled = true
                }
            )
        }
    }
}

@ExperimentalGetImage
private fun scanningCoroutine(
    view: PreviewView,
    executor: ExecutorService,
    context: Context,
    scanningEnabled: Boolean,
    onQrDetected: (String) -> Unit
) {
    val scanner = BarcodeScanning.getClient(BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build())

    val cameraController = LifecycleCameraController(context)
    cameraController.bindToLifecycle(context as LifecycleOwner)

    cameraController.setImageAnalysisAnalyzer(executor) { image ->
        if (scanningEnabled) {
            processImageProxy(
                image,
                scanner,
                { barcode ->
                    barcode.displayValue?.let { onQrDetected(it) }
                },
                { Log.d("Scan Screen", "No QR code found") }
            )
        } else {
            Log.d("Scan Screen", "Scanning disabled, skipping analysis")
            image.close()
        }
    }

    view.controller = cameraController
}
