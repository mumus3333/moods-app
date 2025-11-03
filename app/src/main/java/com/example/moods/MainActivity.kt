package com.example.moods

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.moods.ui.screens.home.HomeScreen
import com.example.moods.ui.theme.MoodsTheme // Asegúrate de que este archivo exista
import dagger.hilt.android.AndroidEntryPoint

/**
 * Actividad Principal.
 * [ACTUALIZADO] Se añade @AndroidEntryPoint para habilitar Hilt.
 */
@AndroidEntryPoint // Requerido por Hilt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 'MoodsTheme' es generado automáticamente por Android Studio
            // Si no lo tienes, puedes reemplazarlo temporalmente con:
            // MaterialTheme { ... }
            MoodsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Cargamos la pantalla principal que contiene la navegación
                    HomeScreen()
                }
            }
        }
    }
}
