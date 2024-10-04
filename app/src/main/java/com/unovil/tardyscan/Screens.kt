package com.unovil.tardyscan

sealed class Screens(val route: String) {
    object Scan : Screens("scan_route")
    object History : Screens("history_route")
    object Settings : Screens("settings_route")
}