package com.unovil.tardyscan.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBarMain(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        BottomNavigationItems(LocalContext.current)
            .forEach { navItem ->
                val isSelected = currentRoute == navItem.route.routeName
                NavigationBarItem(
                    icon = {
                        Icon(
                            if (isSelected) navItem.selectedIcon else navItem.unselectedIcon,
                            contentDescription = navItem.label
                        )
                    },
                    label = { Text(navItem.label) },
                    selected = isSelected,
                    onClick = {
                        if (!isSelected)
                            navController.navigate(navItem.route.routeName) {
                                launchSingleTop = true
                                restoreState = true
                            }
                    }
                )
            }
    }
}