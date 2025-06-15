package com.unovil.tardyscan.presentation.feature.scan

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unovil.tardyscan.domain.usecase.GetStudentInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val getStudentInfoUseCase: GetStudentInfoUseCase
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
        Log.d("ScanViewModel", "qr code string is: $qrCode")
        viewModelScope.launch {
            val studentInfo = getStudentInfoUseCase.execute(GetStudentInfoUseCase.Input(qrCode))
            if (studentInfo is GetStudentInfoUseCase.Output.Success) {
                Log.d("ScanViewModel", "Success! Student info: ${studentInfo.student}")
                _isScanningEnabled.value = false
                _code.value = qrCode
            } else {
                Log.d("ScanViewModel", "Failure! Student info not found, ${studentInfo.javaClass}")
            }
        }
    }
}