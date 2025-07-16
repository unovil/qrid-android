package com.unovil.tardyscan.presentation.feature.scan

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unovil.tardyscan.domain.model.Attendance
import com.unovil.tardyscan.domain.model.Student
import com.unovil.tardyscan.domain.usecase.CreateAttendanceUseCase
import com.unovil.tardyscan.domain.usecase.GetStudentInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val getStudentInfoUseCase: GetStudentInfoUseCase,
    private val createAttendanceUseCase: CreateAttendanceUseCase,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _isScanningEnabled = MutableStateFlow(true)
    val isScanningEnabled = _isScanningEnabled.asStateFlow()

    private val _hasNavigatedToDialog = MutableStateFlow(false)

    private val _isSubmittingEnabled = MutableStateFlow(true)
    val isSubmittingEnabled = _isSubmittingEnabled.asStateFlow()

    private val _scannedStudent = MutableStateFlow<Student?>(null)
    val scannedStudent = _scannedStudent.asStateFlow()

    private val _returnColor = MutableStateFlow<Color?>(null)
    val returnColor = _returnColor.asStateFlow()

    fun onQrCodeScanned(qrCode: String, actionOnSuccess: () -> Unit) {
        if (_hasNavigatedToDialog.value) return

        Log.d("ScanViewModel", "qr code string is: $qrCode")
        _isScanningEnabled.value = false
        viewModelScope.launch {
            val studentInfo = getStudentInfoUseCase.execute(GetStudentInfoUseCase.Input(qrCode))
            if (studentInfo is GetStudentInfoUseCase.Output.Success) {
                Log.d("ScanViewModel", "Success! Student info: ${studentInfo.student}")
                _scannedStudent.value = studentInfo.student
                _hasNavigatedToDialog.value = true
                actionOnSuccess()
            } else {
                val errorMessage = when (studentInfo) {
                    is GetStudentInfoUseCase.Output.Failure.InvalidCode, is GetStudentInfoUseCase.Output.Failure.InvalidDecryption -> "Invalid QR Code!"
                    is GetStudentInfoUseCase.Output.Failure.NotFound -> "Student not found!"
                    is GetStudentInfoUseCase.Output.Failure.HttpRequestError -> "Can't connect. Please use a stable connection!"
                    is GetStudentInfoUseCase.Output.Failure.HttpRequestTimeout -> "Request timed out! Please try again."
                    else -> "Something happened on our end. Please try again."
                }

                Toast.makeText(appContext, errorMessage, Toast.LENGTH_SHORT).show()
                _returnColor.value = Color.Red
                _isScanningEnabled.value = true
            }
        }
    }

    fun onSubmitAttendance(onSuccess: () -> Unit, onDuplicate: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            _isSubmittingEnabled.value = false

            try {
                val isSubmitted = createAttendanceUseCase.execute(
                    CreateAttendanceUseCase.Input(
                        Attendance(_scannedStudent.value!!.id, Clock.System.now())
                    )
                )

                when (isSubmitted) {
                    is CreateAttendanceUseCase.Output.Success -> {
                        _returnColor.value = Color.Green
                        onSuccess()
                    }
                    is CreateAttendanceUseCase.Output.Failure.Duplication -> {
                        Log.e("ScanViewModel", "Duplicate attendance already exists!")
                        _returnColor.value = Color.Yellow
                        onDuplicate()
                    }
                    is CreateAttendanceUseCase.Output.Failure -> {
                        Log.e("ScanViewModel", "Failed to submit attendance")
                        _returnColor.value = Color.Red
                        onFailure()
                    }
                }
            } finally {
                _isSubmittingEnabled.value = true
            }
        }
    }

    fun resetReturnColor() {
        _returnColor.value = null
    }

    fun onReset() {
        _isSubmittingEnabled.value = true
        _isScanningEnabled.value = true
        _scannedStudent.value = null
    }

    fun resetNavigationFlag() {
        _hasNavigatedToDialog.value = false
    }
}
