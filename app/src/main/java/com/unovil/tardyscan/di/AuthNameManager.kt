package com.unovil.tardyscan.di

import com.unovil.tardyscan.data.repository.AuthenticationRepository
import com.unovil.tardyscan.domain.model.User
import dagger.Lazy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthNameManager @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authenticationRepository: Lazy<AuthenticationRepository>
) {
    private val _allowedUserName: MutableStateFlow<String?> = MutableStateFlow(null)
    val allowedUserName = _allowedUserName.asStateFlow()

    private val _allowedUser: MutableStateFlow<User?> = MutableStateFlow(null)
    val allowedUser = _allowedUser.asStateFlow()

    suspend fun loadAllowedUser() {
        _allowedUserName.value = settingsRepository.nameFlow.first()
        authenticationRepository.get().updateAllowedUser()
    }

    suspend fun setAllowedUser(user: User?) {
        _allowedUser.value = user
        when (user) {
            is User.Student -> {
                _allowedUserName.value = user.student.firstName
                settingsRepository.setName(user.student.firstName)
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
}