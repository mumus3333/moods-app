package com.example.moods.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Define las rutas de navegación y la información de las pestañas
 * de la barra de navegación inferior (BottomBar).
 */
sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    // Pestaña 1: Registrar
    object Entry : Screen(
        route = "entry",
        title = "Registrar",
        icon = Icons.Default.Edit
    )

    // Pestaña 2: Análisis
    object Analytics : Screen(
        route = "analytics",
        title = "Análisis",
        icon = Icons.Default.Face
    )
}

// Lista de pantallas para la barra de navegación
val bottomBarScreens = listOf(
    Screen.Entry,
    Screen.Analytics,
)
