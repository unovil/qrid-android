package com.unovil.tardyscan.presentation.feature.scan

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(

) : ViewModel() {

    private val _isScanningEnabled = MutableStateFlow(true)
    val isScanningEnabled = _isScanningEnabled.asStateFlow()

    private val _code = MutableStateFlow<String?>(null)
    val code = _code.asStateFlow()

    fun onReset() {
        _isScanningEnabled.value = true
        _code.value = null
    }

    fun onQrCodeScanned(qrCode: String) {
        _isScanningEnabled.value = false
        _code.value = qrCode
    }
}