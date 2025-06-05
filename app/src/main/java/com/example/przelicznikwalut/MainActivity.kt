package com.example.przelicznikwalut

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.przelicznikwalut.ui.CurrencyConverterScreen
import com.example.przelicznikwalut.ui.RatesScreen
import com.example.przelicznikwalut.ui.theme.CurrencyConverterTheme
import com.example.przelicznikwalut.ui.BottomNavItem
import com.example.przelicznikwalut.ui.HistoricalRatesScreen
import com.example.przelicznikwalut.ui.isRouteSelected

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrencyConverterTheme {
                val navController = rememberNavController()
                val items = listOf(
                    BottomNavItem("converter", stringResource(R.string.converter_screen_label), Icons.Default.Home),
                    BottomNavItem("favorites", stringResource(R.string.favorites_screen_label), Icons.Default.Star)
                )

                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(items, navController)
                    }
                ) { padding ->
                    NavHost(
                        navController = navController,
                        startDestination = "converter",
                        modifier = Modifier.padding(padding)
                    ) {
                        composable("converter") { CurrencyConverterScreen() }
                        composable("favorites") { RatesScreen(navController) }
                        composable(
                            route = "history/{code}",
                            arguments = listOf(
                                navArgument("code") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val code = backStackEntry.arguments?.getString("code") ?: return@composable
                            HistoricalRatesScreen(
                                code = code,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentDestination.isRouteSelected(item.route),
                onClick = {
                    if (!currentDestination.isRouteSelected(item.route)) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
