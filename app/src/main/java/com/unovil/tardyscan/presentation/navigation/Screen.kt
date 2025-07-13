package com.unovil.tardyscan.presentation.navigation

import kotlinx.serialization.Serializable

val Screen.routeName: String
    get() = this::class.simpleName ?: error("Unnamed Screen")

@Serializable
sealed class Screen() {

    // for main screen
    @Serializable object History : Screen()
    @Serializable object Settings : Screen()

    // for auth screen
    @Serializable object VerifyGivenCredentials : Screen()
    @Serializable object SignUp : Screen()
    @Serializable object SignIn : Screen()

    // for scan screen
    @Serializable object Scanning : Screen()
    @Serializable object SuccessfulScan : Screen()
}