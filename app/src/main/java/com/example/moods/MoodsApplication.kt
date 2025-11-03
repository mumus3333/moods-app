package com.example.moods

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase de Aplicación base requerida por Hilt.
 * La anotación @HiltAndroidApp genera el código necesario
 * para la inyección de dependencias.
 */
@HiltAndroidApp
class MoodsApplication : Application() {
    // No se necesita código adicional aquí por ahora.
}
