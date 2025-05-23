package com.unovil.tardyscan.presentation.feature.scan

import android.content.Context
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.unovil.tardyscan.domain.helpers.processImageProxy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ExecutorService
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(

) : ViewModel() {

    private val _isScanningEnabled = MutableStateFlow(true)
    val isScanningEnabled = _isScanningEnabled.asStateFlow()

    private val _scanValue = MutableStateFlow<String?>(null)
    val scanValue = _scanValue.asStateFlow()

    @ExperimentalGetImage
    fun onScan(
        view: PreviewView,
        executor: ExecutorService,
        context: Context,
        onSuccess: () -> Unit
    ) {
        val scanner = BarcodeScanning.getClient(BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build())

        val cameraController = LifecycleCameraController(context)
        cameraController.bindToLifecycle(context as LifecycleOwner)

        cameraController.setImageAnalysisAnalyzer(executor) { image ->
            processImageProxy(
                image,
                scanner,
                {
                    _scanValue.value = it.displayValue
                    _isScanningEnabled.value = false

                    if (_scanValue.value != null && _scanValue.value!!.isNotEmpty()) onSuccess()
                },
                { Log.d("Scan Screen", "No QR code found") }
            )
        }

        view.controller = cameraController
    }

    fun onReset() {
        _isScanningEnabled.value = true
        _scanValue.value = null
    }

}