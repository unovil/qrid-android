package com.unovil.tardyscan.core.data.di

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