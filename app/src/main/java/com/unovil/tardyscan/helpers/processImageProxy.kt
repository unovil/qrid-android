package com.unovil.tardyscan.helpers

import android.graphics.Rect
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
            for (barcode in barcodes) {
                val boundingBox = barcode.boundingBox
                if (boundingBox == null) continue
                if (!isQrInCenterRegion(Pair(imageProxy.width, imageProxy.height), boundingBox)) continue

                onSuccess(barcode)
            }
        }
        .addOnFailureListener {
            onFailure()
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}

private fun isQrInCenterRegion(imageDim: Pair<Int, Int>, qrBoundingBox: Rect): Boolean {
    val centerX = imageDim.first / 2
    val centerY = imageDim.second / 2
    val regionSize = 200 // region size pixels here

    val centerRegion = Rect(
        centerX - regionSize / 2,
        centerY - regionSize / 2,
        centerX + regionSize / 2,
        centerY + regionSize / 2
    )

    return centerRegion.contains(qrBoundingBox.centerX(), qrBoundingBox.centerY())
}