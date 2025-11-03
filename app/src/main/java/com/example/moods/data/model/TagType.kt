package com.example.moods.data.model

/**
 * Enum para clasificar las etiquetas (ActivityTag).
 * Esto nos permite filtrar o mostrar iconos diferentes para "Lugares" vs "Actividades".
 */
enum class TagType {
    ACTIVITY,
    PLACE,
    EVENT,
    PERSON // Añadido por si queremos registrar con quién estábamos
}
