package com.unovil.tardyscan

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItem(
    val label: String = "",
    val selectedIcon: ImageVector = Icons.Default.Home,
    val unselectedIcon: ImageVector = Icons.Outlined.Home,
    val route: String = ""
) {
    fun bottomNavigationItems(): List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = "Scan",
                selectedIcon = Icons.Filled.QrCodeScanner,
                unselectedIcon = Icons.Outlined.QrCodeScanner,
                route = Screens.Scan.route
            ),
            BottomNavigationItem(
                label = "History",
                selectedIcon = Icons.Filled.History,
                unselectedIcon = Icons.Outlined.History,
                route = Screens.History.route
            ),
            BottomNavigationItem(
                label = "Settings",
                selectedIcon = Icons.Filled.Settings,
                unselectedIcon = Icons.Outlined.Settings,
                route = Screens.Settings.route
            )
        )
    }
}
