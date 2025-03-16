package com.unovil.tardyscan.helpers

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

@ExperimentalGetImage
fun processImageProxy(
    imageProxy: ImageProxy,
    scanner: BarcodeScanner,
    onSuccess: (barcode: Barcode) -> Unit,
    onFailure: () -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage == null) {
        imageProxy.close()
        return
    }

    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

    scanner.process(image)
        .addOnSuccessListener { barcodes ->
            if (barcodes.isNotEmpty()) {
                // Just use the first valid barcode we find
                barcodes.firstOrNull()?.let { barcode ->
                    onSuccess(barcode)
                } ?: onFailure()
            } else {
                onFailure()
            }
        }
        .addOnFailureListener {
            onFailure()
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}
