package com.unovil.tardyscan.presentation.feature.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unovil.tardyscan.domain.model.AllowedUser
import com.unovil.tardyscan.domain.usecase.VerifyAllowedUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val verifyAllowedUserUseCase: VerifyAllowedUserUseCase
) : ViewModel() {
    private val _domain = MutableStateFlow("")
    val domain = _domain.asStateFlow()

    private val _domainId = MutableStateFlow("")
    val domainId = _domainId.asStateFlow()

    private val _rawPassword = MutableStateFlow("")
    val rawPassword = _rawPassword.asStateFlow()

    private val _isVerified = MutableStateFlow(false)
    val isVerified = _isVerified.asStateFlow()

    private val _verificationErrorMessage = MutableStateFlow("")
    val verificationErrorMessage = _verificationErrorMessage.asStateFlow()

    fun onDomainChange(domain: String) {
        _domain.value = domain
    }

    fun onDomainIdChange(domainId: String) {
        _domainId.value = domainId
    }

    fun onPasswordChange(rawPassword: String) {
        _rawPassword.value = rawPassword
    }

    fun onVerifyCredentials() {
        viewModelScope.launch {
            val result = verifyAllowedUserUseCase.execute(
                VerifyAllowedUserUseCase.Input(AllowedUser(
                    domain = _domain.value,
                    domainId = _domainId.value,
                    givenPassword = _rawPassword.value
                ))
            )

            _verificationErrorMessage.value = when (result) {
                is VerifyAllowedUserUseCase.Output.Success -> {
                    ""
                }
                is VerifyAllowedUserUseCase.Output.Failure.AlreadyRegistered -> {
                    "This user has already been registered."
                }
                is VerifyAllowedUserUseCase.Output.Failure.NotFound -> {
                    "No user has been found with the provided credentials." +
                            " Please check your credentials and try again."
                }
                else -> {
                    "An unknown error occurred. Please try again later."
                }
            }

            _isVerified.value = result is VerifyAllowedUserUseCase.Output.Success
        }
    }
}