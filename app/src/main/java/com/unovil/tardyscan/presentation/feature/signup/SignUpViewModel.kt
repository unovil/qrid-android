package com.unovil.tardyscan.presentation.feature.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unovil.tardyscan.domain.model.AllowedUser
import com.unovil.tardyscan.domain.usecase.SignUpUseCase
import com.unovil.tardyscan.domain.usecase.VerifyAllowedUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val verifyAllowedUserUseCase: VerifyAllowedUserUseCase,
    private val signUpUseCase: SignUpUseCase
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

    private val _newEmail = MutableStateFlow("")
    val newEmail = _newEmail.asStateFlow()
    
    private val _newPassword = MutableStateFlow("")
    val newPassword = _newPassword.asStateFlow()

    private val _signUpErrorMessage = MutableStateFlow("")
    val signUpErrorMessage = _signUpErrorMessage.asStateFlow()

    private val _isSuccessfulSignUp = MutableStateFlow(false)
    val isSuccessfulSignUp = _isSuccessfulSignUp.asStateFlow()

    fun onDomainChange(domain: String) {
        _domain.value = domain
        _verificationErrorMessage.value = ""
    }

    fun onDomainIdChange(domainId: String) {
        _domainId.value = domainId
        _verificationErrorMessage.value = ""
    }

    fun onPasswordChange(rawPassword: String) {
        _rawPassword.value = rawPassword
        _verificationErrorMessage.value = ""
    }

    fun onNewEmailChange(newEmail: String) {
        _newEmail.value = newEmail
        _verificationErrorMessage.value = ""
        _signUpErrorMessage.value = ""
    }

    fun onNewPasswordChange(newPassword: String) {
        _newPassword.value = newPassword
        _verificationErrorMessage.value = ""
        _signUpErrorMessage.value = ""
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
            Log.d("SignUpVM", "Verification result: ${isVerified.value}")
        }
    }

    fun onSignUp() {
        viewModelScope.launch {
            Log.d("SignUp", "domain: ${_domain.value}")
            Log.d("SignUp", "org number: ${_domainId.value}")
            Log.d("SignUp", "given pw: ${_rawPassword.value}")

            val result = signUpUseCase.execute(
                SignUpUseCase.Input(
                    AllowedUser(
                        domain = _domain.value,
                        domainId = _domainId.value,
                        givenPassword = _rawPassword.value
                    ),
                    _newEmail.value,
                    _newPassword.value
                )
            )

            _signUpErrorMessage.value = when (result) {
                is SignUpUseCase.Output.Failure.Unverified -> {
                    "An error has occurred while verifying your credentials. " +
                            "Please exit and relaunch the app to try again."
                }

                is SignUpUseCase.Output.Failure.WeakPassword -> {
                    "Your password is too weak. Please ensure it meets the required security standards:\n" +
                            result.reasons.joinToString("\n")
                }

                is SignUpUseCase.Output.Failure.Unknown -> {
                    "An unknown error occurred. Please try again later."
                }

                is SignUpUseCase.Output.Failure.AlreadyExists -> {
                    "This user already exists. Try to sign up with a different email address."
                }

                is SignUpUseCase.Output.Success -> {
                    ""
                }

                is SignUpUseCase.Output.Failure -> throw IllegalStateException("Unknown failure reason")
            }

            _isSuccessfulSignUp.value = result is SignUpUseCase.Output.Success
        }
    }
}