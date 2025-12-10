package com.unovil.tardyscan.di

import com.unovil.tardyscan.domain.model.User
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

    private val _allowedUser: MutableStateFlow<User?> = MutableStateFlow(null)
    val allowedUser = _allowedUser.asStateFlow()

    suspend fun loadAllowedUserName() {
        _allowedUserName.value = settingsRepository.nameFlow.first()
    }

    suspend fun setAllowedUser(user: User?) {
        _allowedUser.value = user
        when (user) {
            is User.Student -> {
                val name = "${user.student.lastName}, ${user.student.firstName} ${user.student.middleName}"
                _allowedUserName.value = name
                settingsRepository.setName(name)
            }
            is User.Administrator -> {
                _allowedUserName.value = user.admin.name
                settingsRepository.setName(user.admin.name ?: "")
            }
            null -> {
                _allowedUserName.value = ""
                settingsRepository.setName("")
            }
        }
    }

    suspend fun setAllowedUserName(name: String) {
        _allowedUserName.value = name
        settingsRepository.setName(name)
    }
}