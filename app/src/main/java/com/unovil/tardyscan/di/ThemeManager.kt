package com.unovil.tardyscan.di

import com.unovil.tardyscan.domain.model.ThemeOptions
import com.unovil.tardyscan.proto.ThemeConfig
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

    suspend fun loadTheme() {
        val themeConfig = settingsRepository.themeFlow.first()
        _isDarkTheme.value = when (themeConfig) {
            ThemeConfig.THEME_CONFIG_LIGHT -> false
            ThemeConfig.THEME_CONFIG_DARK -> true
            else -> null
        }
    }

    suspend fun setTheme(themeOptions: ThemeOptions) {
        _isDarkTheme.value = when (themeOptions) {
            ThemeOptions.LIGHT -> false
            ThemeOptions.DARK -> true
            ThemeOptions.FOLLOW_SYSTEM -> null
            else -> null
        }

        val themeConfig = when (themeOptions) {
            ThemeOptions.LIGHT -> ThemeConfig.THEME_CONFIG_LIGHT
            ThemeOptions.DARK -> ThemeConfig.THEME_CONFIG_DARK
            ThemeOptions.FOLLOW_SYSTEM -> ThemeConfig.THEME_CONFIG_SYSTEM
            else -> ThemeConfig.THEME_CONFIG_UNSPECIFIED
        }
        settingsRepository.setTheme(themeConfig)
    }
}