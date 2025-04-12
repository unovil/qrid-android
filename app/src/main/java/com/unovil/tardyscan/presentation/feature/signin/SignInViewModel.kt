package com.unovil.tardyscan.presentation.feature.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unovil.tardyscan.domain.usecase.SignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _signInErrorMessage = MutableStateFlow("")
    val signInErrorMessage = _signInErrorMessage.asStateFlow()

    private val _isSuccessfulSignIn = MutableStateFlow(false)
    val isSuccessfulSignIn = _isSuccessfulSignIn.asStateFlow()

    fun onEmailChange(email: String) {
        _email.value = email
        _signInErrorMessage.value = ""
    }

    fun onPasswordChange(password: String) {
        _password.value = password
        _signInErrorMessage.value = ""
    }

    fun onSignIn() {
        viewModelScope.launch {
            val result = signInUseCase.execute(SignInUseCase.Input(_email.value, _password.value))

            _signInErrorMessage.value = when (result) {
                is SignInUseCase.Output.Failure.AuthError -> "Authentication failed."
                is SignInUseCase.Output.Failure.HttpNetworkError -> "Failed to connect to the server. Please check your internet connection."
                is SignInUseCase.Output.Failure.HttpTimeout -> "Connection timed out. Please try again."
                is SignInUseCase.Output.Failure.Unknown -> "An unknown error occurred. Please try again."
                is SignInUseCase.Output.Success -> ""
            }

            _isSuccessfulSignIn.value = result is SignInUseCase.Output.Success
        }
    }
}