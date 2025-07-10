package com.unovil.tardyscan.di

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor() {
    private val _isDarkTheme: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val isDarkTheme = _isDarkTheme.asStateFlow()

    enum class ThemeMode { LIGHT, DARK, SYSTEM }

    var theme: ThemeMode = ThemeMode.SYSTEM
        set(value) {
            field = value
            when (value) {
                ThemeMode.LIGHT -> _isDarkTheme.value = false
                ThemeMode.DARK -> _isDarkTheme.value = true
                ThemeMode.SYSTEM -> _isDarkTheme.value = null
            }
        }
}