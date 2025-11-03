package com.example.moods.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moods.data.model.ActivityTag
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para las operaciones con ActivityTag.
 */
@Dao
interface ActivityTagDao {

    /**
     * Inserta una nueva etiqueta.
     * Gracias al índice único (name, type) en la entidad ActivityTag,
     * OnConflictStrategy.IGNORE previene duplicados silenciosamente.
     * @return El ID de la etiqueta recién insertada (o -1 si se ignoró).
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: ActivityTag): Long

    /**
     * [CORREGIDO]
     * Obtiene todas las etiquetas de un tipo específico.
     * Acepta un 'String' (ej: "ACTIVITY"), no el objeto TagType.
     */
    @Query("SELECT * FROM activity_tags WHERE type = :type ORDER BY name ASC")
    fun getTagsByType(type: String): Flow<List<ActivityTag>>

    /**
     * Obtiene todas las etiquetas, ordenadas por nombre.
     */
    @Query("SELECT * FROM activity_tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<ActivityTag>>

    /**
     * [CORREGIDO]
     * Busca el ID de una etiqueta existente basado en su nombre y tipo.
     * Acepta 'String' para 'type'.
     */
    @Query("SELECT id FROM activity_tags WHERE name = :name AND type = :type LIMIT 1")
    suspend fun getTagId(name: String, type: String): Long?
}

