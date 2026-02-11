package com.unovil.tardyscan

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unovil.tardyscan.data.network.internetcheck.NetworkMonitor
import com.unovil.tardyscan.domain.usecase.SaveFcmTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    networkMonitor: NetworkMonitor,
    private val saveFcmTokenUseCase: SaveFcmTokenUseCase
) : ViewModel() {

    val isConnected = networkMonitor.isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            true
        )

    fun saveFcmToken(token: String) {
        viewModelScope.launch {
            when (val result = saveFcmTokenUseCase.execute(SaveFcmTokenUseCase.Input(token))) {
                is SaveFcmTokenUseCase.Output.Success -> { }
                is SaveFcmTokenUseCase.Output.Failure -> {
                    Log.e("MainViewModel", "Failed to save FCM token. Reason: ${result.javaClass.name}")
                }
            }
        }
    }
}
