package com.unovil.tardyscan.presentation.feature.onboarding.signup

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unovil.tardyscan.R
import com.unovil.tardyscan.domain.helpers.PasswordValidation
import com.unovil.tardyscan.domain.model.AdministratorUser
import com.unovil.tardyscan.domain.model.StudentUser
import com.unovil.tardyscan.domain.usecase.SignUpAdministratorUseCase
import com.unovil.tardyscan.domain.usecase.SignUpStudentUseCase
import com.unovil.tardyscan.domain.usecase.SignUpUseCase
import com.unovil.tardyscan.domain.usecase.VerifyAdministratorUseCase
import com.unovil.tardyscan.domain.usecase.VerifyStudentUseCase
import com.unovil.tardyscan.domain.usecase.VerifyUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val verifyAdministratorUseCase: VerifyAdministratorUseCase,
    private val verifyStudentUseCase: VerifyStudentUseCase,
    private val signUpAdministratorUseCase: SignUpAdministratorUseCase,
    private val signUpStudentUseCase: SignUpStudentUseCase
) : ViewModel() {
    private val _userMode = MutableStateFlow(SignUpUserMode.ADMINISTRATOR)
    val userMode = _userMode.asStateFlow()

    private val _domain = MutableStateFlow("")
    val domain = _domain.asStateFlow()

    private val _domainId = MutableStateFlow("")
    val domainId = _domainId.asStateFlow()

    private val _lrn = MutableStateFlow("")
    val lrn = _lrn.asStateFlow()

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

    private val _passwordValidations = MutableStateFlow(mapOf(
        context.getString(R.string.password_requirement_length) to false,
        context.getString(R.string.password_requirement_lowercase) to false,
        context.getString(R.string.password_requirement_uppercase) to false,
        context.getString(R.string.password_requirement_number) to false,
        context.getString(R.string.password_requirement_special) to false
    ))
    
    val passwordValidations = _passwordValidations.asStateFlow()
    
    private val _isSignUpButtonEnabled = MutableStateFlow(false)
    val isSignUpButtonEnabled = _isSignUpButtonEnabled.asStateFlow()

    fun onUserModeChange(userMode: SignUpUserMode) {
        _userMode.value = userMode
    }

    fun onDomainChange(domain: String) {
        _domain.value = domain
        _verificationErrorMessage.value = ""
    }

    fun onDomainIdChange(domainId: String) {
        _domainId.value = domainId
        _verificationErrorMessage.value = ""
    }

    fun onLrnChange(lrn: String) {
        if (!lrn.isEmpty() && !lrn.matches(Regex("^\\d+$"))) return

        _lrn.value = lrn
        _verificationErrorMessage.value = ""
    }

    fun onPasswordChange(rawPassword: String) {
        _rawPassword.value = rawPassword
        _verificationErrorMessage.value = ""
    }

    fun onNewEmailChange(newEmail: String) {
        _newEmail.value = newEmail.replace(Regex("\\s"), "")
        _verificationErrorMessage.value = ""
        _signUpErrorMessage.value = ""

        onChangeFormText()
    }

    fun onNewPasswordChange(newPassword: String) {
        _newPassword.value = newPassword
        _verificationErrorMessage.value = ""
        _signUpErrorMessage.value = ""

        _passwordValidations.value = mapOf(
            context.getString(R.string.password_requirement_length) to PasswordValidation.hasMinimumLength(newPassword),
            context.getString(R.string.password_requirement_lowercase) to PasswordValidation.hasLowercase(newPassword),
            context.getString(R.string.password_requirement_uppercase) to PasswordValidation.hasUppercase(newPassword),
            context.getString(R.string.password_requirement_number) to PasswordValidation.hasNumber(newPassword),
            context.getString(R.string.password_requirement_special) to PasswordValidation.hasSpecialCharacter(newPassword)
        )

        onChangeFormText()
    }

    private fun onChangeFormText() {
        _isSignUpButtonEnabled.value = _newEmail.value.matches(
            Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        ) && _passwordValidations.value.values.count { it } == _passwordValidations.value.size
    }

    fun onVerifyCredentials(mode: SignUpUserMode = _userMode.value) {
        if (mode == SignUpUserMode.STUDENT && (_lrn.value.toLongOrNull() == null || _lrn.value.toLongOrNull() == 0L)) {
            _verificationErrorMessage.value = "This LRN is not a numeric value. Please enter a numeric value."
            return
        }

        viewModelScope.launch {
            val result = when (mode) {
                SignUpUserMode.ADMINISTRATOR -> {
                    verifyAdministratorUseCase.execute(
                        VerifyAdministratorUseCase.Input(AdministratorUser(
                            domain = _domain.value,
                            domainId = _domainId.value,
                            givenPassword = _rawPassword.value
                        ))
                    )
                }
                SignUpUserMode.STUDENT -> {
                    verifyStudentUseCase.execute(
                        VerifyStudentUseCase.Input(StudentUser(
                            lrn = _lrn.value.toLong(),
                            givenPassword = _rawPassword.value
                        ))
                    )
                }
            }

            _verificationErrorMessage.value = when (result) {
                is VerifyUserUseCase.Output.Success -> {
                    ""
                }

                is VerifyUserUseCase.Output.Failure.AlreadyRegistered -> {
                    context.getString(R.string.error_duplicate_credentials)
                }

                is VerifyUserUseCase.Output.Failure.NotFound -> {
                    context.getString(R.string.error_invalid_credentials)
                }

                else -> {
                    context.getString(R.string.error_unknown)
                }
            }

            _isVerified.value = result is VerifyUserUseCase.Output.Success

            Log.d("SignUpVM", "Verification result: ${isVerified.value}")
        }
    }

    fun onSignUp(mode: SignUpUserMode = _userMode.value) {
        viewModelScope.launch {
            Log.d("SignUp", "domain: ${_domain.value}")
            Log.d("SignUp", "org number: ${_domainId.value}")
            Log.d("SignUp", "lrn: ${_lrn.value}")
            Log.d("SignUp", "given pw: ${_rawPassword.value}")

            val result = when (mode) {
                SignUpUserMode.ADMINISTRATOR -> {
                    signUpAdministratorUseCase.execute(
                        SignUpAdministratorUseCase.Input(
                            AdministratorUser(
                                domain = _domain.value,
                                domainId = _domainId.value,
                                givenPassword = _rawPassword.value
                            ),
                            _newEmail.value,
                            _newPassword.value
                        )
                    )
                }
                SignUpUserMode.STUDENT -> {
                    signUpStudentUseCase.execute(
                        SignUpStudentUseCase.Input(
                            StudentUser(
                                lrn = _lrn.value.toLong(),
                                givenPassword = _rawPassword.value
                            ),
                            _newEmail.value,
                            _newPassword.value
                        )
                    )
                }
            }

            _signUpErrorMessage.value = when (result) {
                is SignUpUseCase.Output.Failure.Unverified -> {
                    context.getString(R.string.signup_error_unverified)
                }

                is SignUpUseCase.Output.Failure.WeakPassword -> {
                    context.getString(
                        R.string.signup_error_weak_password,
                        result.reasons.joinToString("\n")
                    )
                }

                is SignUpUseCase.Output.Failure.Unknown -> {
                    context.getString(R.string.error_unknown)
                }

                is SignUpUseCase.Output.Failure.AlreadyExists -> {
                    context.getString(R.string.signup_error_duplicate)
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