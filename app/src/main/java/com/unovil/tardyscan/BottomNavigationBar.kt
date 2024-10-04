package com.unovil.tardyscan

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun BottomNavigationBar(navController: NavController) {
    var selectedNavItemState by remember{ mutableIntStateOf(0) }

    NavigationBar {
        BottomNavigationItem().bottomNavigationItems().forEachIndexed { index, navItem ->
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