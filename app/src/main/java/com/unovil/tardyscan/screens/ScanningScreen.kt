package com.unovil.tardyscan.screens

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.unovil.tardyscan.helpers.processImageProxy
import java.util.concurrent.ExecutorService

@ExperimentalGetImage
@Composable
fun ScanningScreen(executor: ExecutorService, onBack: () -> Unit) {
    val context = LocalContext.current
    var previewView by remember { mutableStateOf<PreviewView?>(null) }

    LaunchedEffect(previewView) {
        previewView?.let { view ->
            scanningCoroutine(view, executor, context)
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

        Box (
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            Canvas(modifier = Modifier
                .align(Alignment.Center)
                .size(200.dp)) {
                drawRect(
                    color = Color.Transparent,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
    }

    Button (
        colors = ButtonColors(
            Color.Transparent,
            Color.White,
            Color.Transparent,
            Color.Transparent
        ),
        onClick = { onBack }
    ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
    }
}

@ExperimentalGetImage
private fun scanningCoroutine(
    view: PreviewView,
    executor: ExecutorService,
    context: Context
) {
    val scanner = BarcodeScanning.getClient(BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build())

    val cameraController = LifecycleCameraController(context)
    cameraController.bindToLifecycle(context as LifecycleOwner)

    cameraController.setImageAnalysisAnalyzer(executor) { image ->
        processImageProxy(image, scanner,
            onSuccess = { Log.d("QR Scan", "Successful! ${it.displayValue}") },
            onFailure = { Log.d("QR Scan", "Failed to scan") }
        )
    }

    view.controller = cameraController
}