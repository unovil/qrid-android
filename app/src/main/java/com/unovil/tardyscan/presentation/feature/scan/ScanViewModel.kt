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

    private val _scanValue = MutableStateFlow<String?>(null)
    val scanValue = _scanValue.asStateFlow()

    fun onReset() {
        _isScanningEnabled.value = true
        _scanValue.value = null
    }

}