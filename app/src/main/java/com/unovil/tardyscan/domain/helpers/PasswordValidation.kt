package com.unovil.tardyscan.domain.helpers

object PasswordValidation {
    // Password is at least 8 characters long
    fun hasMinimumLength(password: String): Boolean {
        return password.length >= 8
    }

    // Password contains at least one lowercase letter
    fun hasLowercase(password: String): Boolean {
        return password.matches(Regex(".*[a-z].*"))
    }

    // Password contains at least one uppercase letter
    fun hasUppercase(password: String): Boolean {
        return password.matches(Regex(".*[A-Z].*"))
    }

    // Password contains at least one number
    fun hasNumber(password: String): Boolean {
        return password.matches(Regex(".*\\d.*"))
    }

    // Password contains at least one special character
    fun hasSpecialCharacter(password: String): Boolean {
        return password.matches(Regex(".*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*"))
    }
}