package com.example.moods.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa una única entrada de estado de ánimo en la base de datos.
 * Esta es la tabla principal.
 *
 * @param id Identificador único autogenerado.
 * @param timestamp Marca de tiempo (en milisegundos) de cuándo se creó la entrada.
 * @param moodRating La calificación del estado de ánimo (ej. de 1 a 5).
 * @param notes Notas opcionales que el usuario quiera añadir.
 */
@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val moodRating: Int,
    val notes: String?
)
