package com.unovil.tardyscan.presentation.feature.scan

import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.core.resolutionselector.ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService

@ExperimentalGetImage
@Composable
fun ScanningScreen(
    viewModel: ScanViewModel? = hiltViewModel(),
    executor: ExecutorService,
    isScanningEnabled: State<Boolean> = viewModel!!.isScanningEnabled.collectAsState(),
    code: State<String?> = viewModel!!.code.collectAsState(),
    onQrCodeScanned: (String) -> Unit = { viewModel!!.onQrCodeScanned(it) },
    onBack: () -> Unit,
    onNavigate: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(code.value) {
        if (code.value == null) return@LaunchedEffect
        if (code.value!!.isNotEmpty()) Toast.makeText(context, code.value, Toast.LENGTH_SHORT).show()
        Log.d("ScanningScreen", "Code: ${code.value}")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { context ->
            val cameraController = LifecycleCameraController(context).apply {
                cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                imageAnalysisResolutionSelector = ResolutionSelector.Builder()
                    .setResolutionStrategy(
                        ResolutionStrategy(
                            android.util.Size(720, 1280),
                            FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                        )
                    )
                    .build()

                imageAnalysisBackpressureStrategy = STRATEGY_KEEP_ONLY_LATEST

                setImageAnalysisAnalyzer(executor, QrCodeAnalyzer(
                    enabledState = isScanningEnabled,
                    onQrCodeScanned = onQrCodeScanned,
                    onQrCodeFailed = { Log.d("ScanningScreen", "QR Code scanning failed") }
                ))

                bindToLifecycle(context as LifecycleOwner)
            }
            val previewView = PreviewView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                controller = cameraController
            }

            previewView
        })

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
            enabled = isScanningEnabled.value,
            onClick = onBack
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
        }
    }
}
