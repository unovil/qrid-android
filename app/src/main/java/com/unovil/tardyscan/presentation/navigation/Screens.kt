package com.unovil.tardyscan.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screens(
    val route: String,
    val selectedImage: ImageVector? = null,
    val deselectedImage: ImageVector? = null
) {
    // for main screen
    data object History : Screens("history_route", Icons.Filled.History, Icons.Outlined.History)
    data object Settings : Screens("settings_route", Icons.Filled.Settings, Icons.Outlined.Settings)

    // for auth screen
    data object VerifyGivenCredentials: Screens("verify_given_credentials_route")
    data object SignUp : Screens("sign_up_route")
}