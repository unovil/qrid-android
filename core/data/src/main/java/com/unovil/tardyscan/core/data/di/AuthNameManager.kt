package com.unovil.tardyscan.core.data.di

import com.unovil.tardyscan.core.data.dto.AllowedUserDto
import com.unovil.tardyscan.core.datastore.di.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthNameManager @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    private val _allowedUserName: MutableStateFlow<String?> = MutableStateFlow(null)
    val allowedUserName = _allowedUserName.asStateFlow()

    private val _allowedUser: MutableStateFlow<AllowedUserDto?> = MutableStateFlow(null)
    val allowedUser = _allowedUser.asStateFlow()

    suspend fun loadAllowedUserName() {
        _allowedUserName.value = settingsRepository.nameFlow.first()
    }

    suspend fun setAllowedUser(user: AllowedUserDto) {
        _allowedUser.value = user
        _allowedUserName.value = user.name ?: ""
        settingsRepository.setName(user.name ?: "")
    }

    suspend fun setAllowedUserName(name: String) {
        _allowedUserName.value = name
        settingsRepository.setName(name)
    }
}