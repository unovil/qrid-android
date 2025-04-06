package com.unovil.tardyscan.presentation.navigation

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.unovil.tardyscan.R

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
                selectedIcon = Screens.History.selectedImage!!,
                unselectedIcon = Screens.History.deselectedImage!!,
                route = Screens.History.route
            ),
            BottomNavigationItem(
                label = context.resources.getString(R.string.setting_screen),
                selectedIcon = Screens.Settings.selectedImage!!,
                unselectedIcon = Screens.Settings.deselectedImage!!,
                route = Screens.Settings.route
            )
        )
    }
}
