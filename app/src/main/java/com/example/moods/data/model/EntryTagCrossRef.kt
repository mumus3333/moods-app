package com.example.moods.data.model

import androidx.room.Entity
import androidx.room.Index

/**
 * Tabla de Referencia Cruzada (CrossRef) para la relación Muchos-a-Muchos
 * entre [MoodEntry] y [ActivityTag].
 *
 * Esto nos permite "unir" una entrada de ánimo con múltiples etiquetas.
 *
 * @param moodEntryId El ID de la entrada de ánimo.
 * @param activityTagId El ID de la etiqueta de actividad.
 */
@Entity(
    primaryKeys = ["moodEntryId", "activityTagId"],
    indices = [Index(value = ["activityTagId"])] // Índice para búsquedas eficientes por etiqueta
)
data class EntryTagCrossRef(
    val moodEntryId: Long,
    val activityTagId: Long
)
