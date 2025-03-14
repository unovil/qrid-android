package com.unovil.tardyscan

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class MainScreens(
    val route: String,
    val selectedImage: ImageVector,
    val deselectedImage: ImageVector
) {
    object History : MainScreens("history_route", Icons.Filled.History, Icons.Outlined.History)
    object Settings : MainScreens("settings_route", Icons.Filled.Settings, Icons.Outlined.Settings)
}