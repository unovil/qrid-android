package com.unovil.tardyscan.presentation.feature.signin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unovil.tardyscan.R
import com.unovil.tardyscan.domain.usecase.SignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
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
                is SignInUseCase.Output.Failure.AuthError -> context.getString(R.string.auth_failed)
                is SignInUseCase.Output.Failure.HttpNetworkError -> context.getString(R.string.auth_network_error)
                is SignInUseCase.Output.Failure.HttpTimeout -> context.getString(R.string.auth_network_timeout)
                is SignInUseCase.Output.Failure.Unknown -> context.getString(R.string.auth_unknown_error)
                is SignInUseCase.Output.Success -> ""
            }

            _isSuccessfulSignIn.value = result is SignInUseCase.Output.Success
        }
    }
}