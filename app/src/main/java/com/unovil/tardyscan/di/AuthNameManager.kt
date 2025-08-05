package com.unovil.tardyscan.di

import com.unovil.tardyscan.data.network.dto.AllowedUserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthNameManager @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    private val _allowedUserName: MutableStateFlow<String?> = MutableStateFlow(null)
    val allowedUserName = _allowedUserName.asStateFlow()

    private val _allowedUser: MutableStateFlow<AllowedUserDto?> = MutableStateFlow(null)
    val allowedUser = _allowedUser.asStateFlow()

    suspend fun loadAllowedUserName() {
        _allowedUserName.value = userPreferencesRepository.nameFlow.first()
    }

    suspend fun setAllowedUser(user: AllowedUserDto) {
        _allowedUser.value = user
        _allowedUserName.value = user.name ?: ""
        userPreferencesRepository.setName(user.name ?: "")
    }

    suspend fun setAllowedUserName(name: String) {
        _allowedUserName.value = name
        userPreferencesRepository.setName(name)
    }
}