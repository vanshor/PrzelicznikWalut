package com.example.przelicznikwalut.ui

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

fun NavDestination?.isRouteSelected(route: String): Boolean {
    return this?.hierarchy?.any { it.route == route } == true
}
