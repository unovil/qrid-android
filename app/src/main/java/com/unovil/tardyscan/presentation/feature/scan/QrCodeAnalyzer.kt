package com.unovil.tardyscan.presentation.feature.scan

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.State
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class QrCodeAnalyzer(
    private val enabledState: State<Boolean>,
    private val onQrCodeFailed: () -> Unit = { },
    private val onQrCodeScanned: (String) -> Unit
): ImageAnalysis.Analyzer {

    // qr code format
    private val supportedBarcodeFormats = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val isEnabled = enabledState.value

        Log.d("QrCodeAnalyzer", "isEnabled: $isEnabled")
        if (!isEnabled) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        val scanner = BarcodeScanning.getClient(supportedBarcodeFormats)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isEmpty() || barcodes == null) {
                    Log.d("QrCodeAnalyzer", "No barcode found")
                    onQrCodeFailed()
                    return@addOnSuccessListener
                } else if (barcodes.first().valueType != Barcode.TYPE_TEXT) {
                    Log.d("QrCodeAnalyzer", "Barcode type not supported")
                    onQrCodeFailed()
                } else if (barcodes.first().displayValue.isNullOrBlank()) {
                    Log.d("QrCodeAnalyzer", "Barcode value is blank")
                    onQrCodeFailed()
                } else {
                    onQrCodeScanned(barcodes.first().displayValue!!)
                }
            }
            .addOnFailureListener {
                onQrCodeFailed()
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}