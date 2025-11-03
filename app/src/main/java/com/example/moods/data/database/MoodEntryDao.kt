package com.example.moods.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.moods.data.model.EntryTagCrossRef
import com.example.moods.data.model.MoodEntry
import com.example.moods.data.model.MoodEntryWithTags
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para las operaciones con MoodEntry.
 */
@Dao
interface MoodEntryDao {

    /**
     * Obtiene todos los registros con sus etiquetas, ordenados del más nuevo al más antiguo.
     */
    @Transaction
    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC")
    fun getAllEntriesWithTags(): Flow<List<MoodEntryWithTags>>

    /**
     * Obtiene los registros en un rango de fechas específico.
     */
    @Transaction
    @Query("SELECT * FROM mood_entries WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getEntriesWithTagsInRange(startTime: Long, endTime: Long): Flow<List<MoodEntryWithTags>>

    /**
     * [NUEVO MÉTODO AÑADIDO]
     * Obtiene el registro de ánimo más reciente (el último).
     */
    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestEntry(): MoodEntry?

    /**
     * Inserta una nueva entrada de ánimo.
     * @return El ID de la fila (rowId) recién insertada.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodEntry(entry: MoodEntry): Long

    /**
     * Inserta una nueva relación entre un registro y una etiqueta.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEntryTagCrossRef(crossRef: EntryTagCrossRef)
}

