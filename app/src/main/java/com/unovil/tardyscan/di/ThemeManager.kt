package com.unovil.tardyscan.di

import com.unovil.tardyscan.core.datastore.di.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    private val _isDarkTheme: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val isDarkTheme = _isDarkTheme.asStateFlow()

    enum class ThemeMode { LIGHT, DARK, SYSTEM }

    suspend fun loadTheme() {
        val themeString = settingsRepository.themeFlow.first()
        _isDarkTheme.value = when (themeString) {
            "LIGHT" -> false
            "DARK" -> true
            else -> null
        }
    }

    suspend fun setTheme(theme: ThemeMode) {
        _isDarkTheme.value = when (theme) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> null
        }
        settingsRepository.setTheme(theme.name)
    }
}