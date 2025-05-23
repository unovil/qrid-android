package com.unovil.tardyscan.presentation.navigation

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.unovil.tardyscan.R

data class BottomNavigationItems(
    val label: String = "",
    val selectedIcon: ImageVector = Icons.Default.Home,
    val unselectedIcon: ImageVector = Icons.Outlined.Home,
    val route: Screen
) {
    companion object {
        operator fun invoke(context: Context): List<BottomNavigationItems> {
            return listOf(
                BottomNavigationItems(
                    label = context.resources.getString(R.string.history_screen),
                    selectedIcon = Icons.Filled.History,
                    unselectedIcon = Icons.Outlined.History,
                    route = Screen.History
                ),
                BottomNavigationItems(
                    label = context.resources.getString(R.string.setting_screen),
                    selectedIcon = Icons.Filled.Settings,
                    unselectedIcon = Icons.Outlined.Settings,
                    route = Screen.Settings
                )
            )
        }
    }
}
