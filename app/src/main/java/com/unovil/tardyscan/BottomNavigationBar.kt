package com.unovil.tardyscan

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

@Composable
fun BottomNavigationBar(navController: NavController) {
    var selectedNavItemState by remember{ mutableIntStateOf(0) }

    NavigationBar {
        BottomNavigationItem().bottomNavigationItems(LocalContext.current).forEachIndexed { index, navItem ->
            NavigationBarItem(
                icon = {
                    Icon(
                        if (selectedNavItemState == index) navItem.selectedIcon else navItem.unselectedIcon,
                        contentDescription = navItem.label
                    )
                },
                label = { Text(navItem.label) },
                selected = selectedNavItemState == index,
                onClick = {
                    selectedNavItemState = index
                    navController.navigate(navItem.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}