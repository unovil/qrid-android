package com.unovil.tardyscan.presentation.feature.settings

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unovil.tardyscan.R
import com.unovil.tardyscan.di.AuthNameManager
import com.unovil.tardyscan.di.ThemeManager
import com.unovil.tardyscan.domain.model.ThemeOptions
import com.unovil.tardyscan.domain.model.User
import com.unovil.tardyscan.domain.usecase.GetSignedUserUseCase
import com.unovil.tardyscan.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeManager: ThemeManager,
    private val authNameManager: AuthNameManager,
    private val getSignedUserUseCase: GetSignedUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {
    // val appearanceList = listOf("☀️ Light mode", "🌙 Dark mode", "⚙️ Follow system setting")
    val appearanceList = listOf(
        context.getString(R.string.settings_appearance_option_light),
        context.getString(R.string.settings_appearance_option_dark),
        context.getString(R.string.settings_appearance_option_system),
    )

    private val _selectedAppearance = MutableStateFlow(appearanceList[2])
    val selectedAppearance = _selectedAppearance.asStateFlow()

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile = _userProfile.asStateFlow()

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

    fun onCheckProfile(onFailure: () -> Unit) {
        if (_userProfile.value != null) return

        viewModelScope.launch {
            val result = getSignedUserUseCase.execute(GetSignedUserUseCase.Input())

            if (result !is GetSignedUserUseCase.Output.Success) {
                Log.e("SettingsViewModel", "Failed to get user profile, ${result::class}")
                onFailure()
            } else {
                _userProfile.value = authNameManager.allowedUser.value!!
            }
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
            context.getString(R.string.settings_appearance_option_light) -> ThemeOptions.LIGHT
            context.getString(R.string.settings_appearance_option_dark) -> ThemeOptions.DARK
            context.getString(R.string.settings_appearance_option_system) -> ThemeOptions.FOLLOW_SYSTEM
            else -> ThemeOptions.FOLLOW_SYSTEM
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