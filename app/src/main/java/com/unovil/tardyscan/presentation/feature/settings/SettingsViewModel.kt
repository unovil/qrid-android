package com.unovil.tardyscan.presentation.feature.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unovil.tardyscan.di.ThemeManager
import com.unovil.tardyscan.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeManager: ThemeManager,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {
    val appearanceList = listOf("☀️ Light mode", "🌙 Dark mode", "⚙️ Follow system setting")

    private val _selectedAppearance = MutableStateFlow(appearanceList[2])
    val selectedAppearance = _selectedAppearance.asStateFlow()

    private val _newAppearance = MutableStateFlow(appearanceList[2])
    val newAppearance = _newAppearance.asStateFlow()

    init {
        viewModelScope.launch {
            themeManager.loadTheme() // Start listening

            val currentAppearance = when (themeManager.isDarkTheme.value) {
                false -> appearanceList[0]
                true -> appearanceList[1]
                null -> appearanceList[2]
            }

            _selectedAppearance.value = currentAppearance
            _newAppearance.value = currentAppearance
        }
    }

    fun onUpdateAppearance(appearance: String) {
        _newAppearance.value = appearance
    }

    fun onCancelAppearance() {
        _newAppearance.value = _selectedAppearance.value
    }

    fun onSetAppearance() {
        val themeMode = when (_newAppearance.value) {
            "☀️ Light mode" -> ThemeManager.ThemeMode.LIGHT
            "🌙 Dark mode" -> ThemeManager.ThemeMode.DARK
            "⚙️ Follow system setting" -> ThemeManager.ThemeMode.SYSTEM
            else -> ThemeManager.ThemeMode.SYSTEM
        }

        viewModelScope.launch {
            themeManager.setTheme(themeMode)
        }

        _selectedAppearance.value = _newAppearance.value
    }

    fun onLogOut(onFailure: () -> Unit) {
        viewModelScope.launch {
            val result = signOutUseCase.execute(SignOutUseCase.Input())
            if (result !is SignOutUseCase.Output.Success) {
                Log.e("SettingsViewModel", "Failed to log out, ${result::class}")
                onFailure()
            }
        }
    }

}