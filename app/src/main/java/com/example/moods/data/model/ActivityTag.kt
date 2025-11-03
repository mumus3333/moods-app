package com.example.moods.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Representa una etiqueta que el usuario puede asociar a un MoodEntry.
 * (Ej: "Trabajo", "Casa", "Correr", "Café con amigos").
 *
 * @param id Identificador único autogenerado.
 * @param name El texto de la etiqueta (ej: "Correr").
 * @param type El tipo de etiqueta (ej: "ACTIVITY", "LOCATION", "EVENT").
 * Usamos un índice único compuesto (name, type) para prevenir
 * duplicados (ej: no pueden existir dos "Trabajo" de tipo "LOCATION").
 */
@Entity(
    tableName = "activity_tags",
    indices = [Index(value = ["name", "type"], unique = true)]
)
data class ActivityTag(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String // [CORREGIDO] Debe ser String
)

