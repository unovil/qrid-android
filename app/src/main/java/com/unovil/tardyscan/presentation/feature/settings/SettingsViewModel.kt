package com.unovil.tardyscan.presentation.feature.settings

import androidx.lifecycle.ViewModel
import com.unovil.tardyscan.di.ThemeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeManager: ThemeManager
) : ViewModel() {
    val appearanceList = listOf("☀️ Light mode", "🌙 Dark mode", "⚙️ Follow system setting")

    private val _selectedAppearance = MutableStateFlow(appearanceList[2])
    val selectedAppearance = _selectedAppearance.asStateFlow()

    private val _newAppearance = MutableStateFlow(appearanceList[2])
    val newAppearance = _newAppearance.asStateFlow()

    fun onUpdateAppearance(appearance: String) {
        _newAppearance.value = appearance
    }

    fun onCancelAppearance() {
        _newAppearance.value = _selectedAppearance.value
    }

    fun onSetAppearance() {
        themeManager.theme = when (_newAppearance.value) {
            "☀️ Light mode" -> ThemeManager.ThemeMode.LIGHT
            "🌙 Dark mode" -> ThemeManager.ThemeMode.DARK
            "⚙️ Follow system setting" -> ThemeManager.ThemeMode.SYSTEM
            else -> ThemeManager.ThemeMode.SYSTEM
        }

        _selectedAppearance.value = _newAppearance.value
    }

}