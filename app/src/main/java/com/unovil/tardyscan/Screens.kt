package com.unovil.tardyscan

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screens(
    val route: String,
    val selectedImage: ImageVector,
    val deselectedImage: ImageVector
) {
    object Scan : Screens("scan_route", Icons.Filled.QrCodeScanner, Icons.Outlined.QrCodeScanner)
    object History : Screens("history_route", Icons.Filled.History, Icons.Outlined.History)
    object Settings : Screens("settings_route", Icons.Filled.Settings, Icons.Outlined.Settings)
}