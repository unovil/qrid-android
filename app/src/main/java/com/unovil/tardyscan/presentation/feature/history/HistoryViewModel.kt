package com.unovil.tardyscan.presentation.feature.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unovil.tardyscan.domain.usecase.GetStudentInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getStudentInfoUseCase: GetStudentInfoUseCase
) : ViewModel() {

    fun testFunction() {
        viewModelScope.launch {
            val result = getStudentInfoUseCase.execute(GetStudentInfoUseCase.Input(999999999999))

            if (result is GetStudentInfoUseCase.Output.Failure) {
                Log.d("HistoryViewModel", "Success! Failed to get student info for 999999999999")
            } else {
                Log.d("HistoryViewModel", "Failure! Something went wrong. Result: $result")
            }

            val result2 = getStudentInfoUseCase.execute(GetStudentInfoUseCase.Input(100730136315))

            if (result2 is GetStudentInfoUseCase.Output.Success) {
                Log.d("HistoryViewModel", "Success! Got student info for 100730136315: ${result2.student}")
            } else {
                Log.d("HistoryViewModel", "Failure! Something went wrong. Result: $result2")
            }
        }
    }

}