package com.unovil.tardyscan

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItem(
    val label: String = "",
    val selectedIcon: ImageVector = Icons.Default.Home,
    val unselectedIcon: ImageVector = Icons.Outlined.Home,
    val route: String = ""
) {
    fun bottomNavigationItems(context: Context): List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = context.resources.getString(R.string.history_screen),
                selectedIcon = MainScreens.History.selectedImage,
                unselectedIcon = MainScreens.History.deselectedImage,
                route = MainScreens.History.route
            ),
            BottomNavigationItem(
                label = context.resources.getString(R.string.setting_screen),
                selectedIcon = MainScreens.Settings.selectedImage,
                unselectedIcon = MainScreens.Settings.deselectedImage,
                route = MainScreens.Settings.route
            )
        )
    }
}
