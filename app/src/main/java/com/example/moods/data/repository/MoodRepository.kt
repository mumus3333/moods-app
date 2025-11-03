package com.example.moods.data.repository

import com.example.moods.data.model.ActivityTag
import com.example.moods.data.model.MoodEntry
import com.example.moods.data.model.MoodEntryWithTags
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz para la capa de Repositorio.
 * Define las operaciones de datos que la UI (ViewModels) puede solicitar.
 */
interface MoodRepository {

    /**
     * Obtiene todos los registros de ánimo con sus etiquetas asociadas.
     */
    fun getAllEntriesWithTags(): Flow<List<MoodEntryWithTags>>

    /**
     * Obtiene los registros de ánimo dentro de un rango de fechas.
     */
    fun getEntriesWithTagsInRange(startTime: Long, endTime: Long): Flow<List<MoodEntryWithTags>>

    /**
     * Obtiene todas las etiquetas de un tipo específico (ej: "ACTIVITY", "LOCATION").
     *
     * [CORREGIDO] La firma de esta función ahora acepta un String.
     */
    fun getTagsByType(type: String): Flow<List<ActivityTag>>

    /**
     * Guarda un nuevo registro de ánimo y sus etiquetas asociadas.
     */
    suspend fun addNewEntry(entry: MoodEntry, tags: List<ActivityTag>)
}

