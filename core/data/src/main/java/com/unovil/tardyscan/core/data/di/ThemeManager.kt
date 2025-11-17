package com.unovil.tardyscan.core.data.di

import com.unovil.tardyscan.core.datastore.di.SettingsRepository
import com.unovil.tardyscan.core.datastore.proto.ThemeConfig
import com.unovil.tardyscan.core.model.Theme
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

    suspend fun setTheme(theme: Theme) {
        _isDarkTheme.value = when (theme) {
            Theme.LIGHT -> false
            Theme.DARK -> true
            Theme.FOLLOW_SYSTEM -> null
            else -> null
        }

        val themeConfig = when (theme) {
            Theme.LIGHT -> ThemeConfig.THEME_CONFIG_LIGHT
            Theme.DARK -> ThemeConfig.THEME_CONFIG_DARK
            Theme.FOLLOW_SYSTEM -> ThemeConfig.THEME_CONFIG_SYSTEM
            else -> ThemeConfig.THEME_CONFIG_UNSPECIFIED
        }
        settingsRepository.setTheme(themeConfig)
    }
}