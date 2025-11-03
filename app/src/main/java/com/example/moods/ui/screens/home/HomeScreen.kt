package com.example.moods.ui.screens.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moods.ui.navigation.Screen
import com.example.moods.ui.navigation.bottomBarScreens
import com.example.moods.ui.screens.analytics.AnalyticsScreen
import com.example.moods.ui.screens.entry.EntryScreen

/**
 * La pantalla principal que contiene la navegación por pestañas (BottomBar).
 * Esta pantalla gestiona el NavHost (el contenedor de las pantallas).
 */
@Composable
fun HomeScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomBarScreens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Evita múltiples copias de la misma pantalla
                                launchSingleTop = true
                                // Restaura el estado al volver a seleccionar la pestaña
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // NavHost que renderiza la pantalla correcta según la ruta
        NavHost(
            navController,
            startDestination = Screen.Entry.route, // Empezar en "Registrar"
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.Entry.route) {
                EntryScreen()
            }
            composable(Screen.Analytics.route) {
                AnalyticsScreen()
            }
        }
    }
}
