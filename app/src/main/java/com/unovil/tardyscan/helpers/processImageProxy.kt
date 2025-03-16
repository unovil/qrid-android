package com.unovil.tardyscan.helpers

import android.graphics.Rect
import android.graphics.RectF
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

@ExperimentalGetImage
fun processImageProxy(
    imageProxy: ImageProxy,
    scanner: BarcodeScanner,
    scanRegion: Rect,
    onSuccess: (barcode: Barcode) -> Unit,
    onFailure: () -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage == null) {
        imageProxy.close()
        return
    }

    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
    val imageWidth = imageProxy.width
    val imageHeight = imageProxy.height
    val rotationDegrees = imageProxy.imageInfo.rotationDegrees

    scanner.process(image)
        .addOnSuccessListener { barcodes ->
            var foundValidBarcode = false
            
            for (barcode in barcodes) {
                val boundingBox = barcode.boundingBox ?: continue
                
                val isInCenter = isQrInCenterRegion(boundingBox, scanRegion, imageWidth, imageHeight, rotationDegrees)

                if (isInCenter) {
                    onSuccess(barcode)
                    foundValidBarcode = true
                    break
                }
            }
            
            if (!foundValidBarcode && barcodes.isNotEmpty()) {
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

private fun isQrInCenterRegion(
    qrBox: Rect, 
    scanRegion: Rect, 
    imageWidth: Int, 
    imageHeight: Int, 
    rotationDegrees: Int
): Boolean {
    val normalizedQrBox = RectF(
        qrBox.left.toFloat() / imageWidth,
        qrBox.top.toFloat() / imageHeight,
        qrBox.right.toFloat() / imageWidth,
        qrBox.bottom.toFloat() / imageHeight
    )
    
    val normalizedScanRegion = RectF(
        scanRegion.left.toFloat() / imageWidth,
        scanRegion.top.toFloat() / imageHeight,
        scanRegion.right.toFloat() / imageWidth,
        scanRegion.bottom.toFloat() / imageHeight
    )
    
    val rotatedQrBox = when (rotationDegrees) {
        90 -> RectF(
            normalizedQrBox.top,
            1f - normalizedQrBox.right,
            normalizedQrBox.bottom,
            1f - normalizedQrBox.left
        )
        180 -> RectF(
            1f - normalizedQrBox.right,
            1f - normalizedQrBox.bottom,
            1f - normalizedQrBox.left,
            1f - normalizedQrBox.top
        )
        270 -> RectF(
            1f - normalizedQrBox.bottom,
            normalizedQrBox.left,
            1f - normalizedQrBox.top,
            normalizedQrBox.right
        )
        else -> normalizedQrBox
    }

    
    val overlap = RectF()
    val hasOverlap = overlap.setIntersect(rotatedQrBox, normalizedScanRegion)
    
    if (!hasOverlap) {
        return false
    }
    
    val qrArea = (rotatedQrBox.width() * rotatedQrBox.height())
    val overlapArea = (overlap.width() * overlap.height())
    val percentageInside = overlapArea / qrArea
    
    val qrCenterX = rotatedQrBox.centerX()
    val qrCenterY = rotatedQrBox.centerY()
    val centerInRegion = normalizedScanRegion.contains(qrCenterX, qrCenterY)
    
    return centerInRegion || percentageInside > 0.4f
}