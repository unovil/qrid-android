package com.unovil.tardyscan

import android.content.Context
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
import androidx.compose.ui.res.stringResource

data class BottomNavigationItem(
    val label: String = "",
    val selectedIcon: ImageVector = Icons.Default.Home,
    val unselectedIcon: ImageVector = Icons.Outlined.Home,
    val route: String = ""
) {
    fun bottomNavigationItems(context: Context): List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = context.resources.getString(R.string.scan_screen),
                selectedIcon = Screens.Scan.selectedImage,
                unselectedIcon = Screens.Scan.deselectedImage,
                route = Screens.Scan.route
            ),
            BottomNavigationItem(
                label = context.resources.getString(R.string.history_screen),
                selectedIcon = Screens.History.selectedImage,
                unselectedIcon = Screens.History.deselectedImage,
                route = Screens.History.route
            ),
            BottomNavigationItem(
                label = context.resources.getString(R.string.setting_screen),
                selectedIcon = Screens.Settings.selectedImage,
                unselectedIcon = Screens.Settings.deselectedImage,
                route = Screens.Settings.route
            )
        )
    }
}
