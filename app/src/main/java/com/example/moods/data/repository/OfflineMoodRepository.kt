package com.example.moods.data.repository

import com.example.moods.data.database.ActivityTagDao
import com.example.moods.data.database.MoodEntryDao
import com.example.moods.data.model.ActivityTag
import com.example.moods.data.model.EntryTagCrossRef
import com.example.moods.data.model.MoodEntry
import com.example.moods.data.model.MoodEntryWithTags
// [CORREGIDO] Ya no necesitamos importar 'TagType' para 'getTagsByType'
// import com.example.moods.data.model.TagType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementación "Offline" del [MoodRepository].
 * Utiliza los DAOs de Room para obtener y guardar datos localmente.
 *
 * @param moodEntryDao DAO para operaciones de MoodEntry.
 * @param activityTagDao DAO para operaciones de ActivityTag.
 */
class OfflineMoodRepository @Inject constructor(
    private val moodEntryDao: MoodEntryDao,
    private val activityTagDao: ActivityTagDao
) : MoodRepository {

    override fun getAllEntriesWithTags(): Flow<List<MoodEntryWithTags>> {
        return moodEntryDao.getAllEntriesWithTags()
    }

    override fun getEntriesWithTagsInRange(startTime: Long, endTime: Long): Flow<List<MoodEntryWithTags>> {
        return moodEntryDao.getEntriesWithTagsInRange(startTime, endTime)
    }

    /**
     * [CORREGIDO]
     * La firma de la función ahora coincide con la interfaz MoodRepository.
     * Acepta un 'String' en lugar de 'TagType'.
     */
    override fun getTagsByType(type: String): Flow<List<ActivityTag>> {
        // El cuerpo de la función ahora es correcto,
        // porque 'type' es un String y el DAO espera un String.
        return activityTagDao.getTagsByType(type)
    }

    /**
     * Lógica clave para guardar un registro y sus etiquetas.
     * 1. Inserta el MoodEntry y obtiene su ID.
     * 2. Itera sobre cada ActivityTag:
     * a. Intenta insertarla (OnConflict.IGNORE).
     * b. Si se ignoró (tagId == -1), significa que ya existía.
     * c. Busca el ID del tag existente usando el nuevo método getTagId().
     * d. Crea la relación cruzada (CrossRef) con los IDs correctos.
     */
    override suspend fun addNewEntry(entry: MoodEntry, tags: List<ActivityTag>) {
        // 1. Insertar la entrada principal y obtener su ID
        val entryId = moodEntryDao.insertMoodEntry(entry)

        // 2. Procesar y vincular cada etiqueta
        tags.forEach { tag ->
            // 2a. Intentar insertar la etiqueta
            var tagId = activityTagDao.insertTag(tag)

            // 2b. Si se ignoró (conflicto, ya existía)
            if (tagId == -1L) {
                // 2c. Buscar el ID de la etiqueta existente
                // (Esto funciona porque ActivityTag.kt (Canvas) define 'type' como String)
                tagId = activityTagDao.getTagId(tag.name, tag.type)
                    ?: throw IllegalStateException("No se pudo encontrar el ID de la etiqueta existente: ${tag.name}")
            }

            // 2d. Crear la relación
            moodEntryDao.insertEntryTagCrossRef(
                EntryTagCrossRef(
                    moodEntryId = entryId,
                    activityTagId = tagId
                )
            )
        }
    }
}

