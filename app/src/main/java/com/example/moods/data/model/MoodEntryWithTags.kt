package com.example.moods.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * Esta es una clase "POJO" (Plain Old Kotlin Object), NO es una tabla.
 * Room la usa para encapsular el resultado de una consulta (query)
 * que une una [MoodEntry] con sus [ActivityTag] asociadas.
 *
 * @param entry La entrada de ánimo (tabla "padre").
 * @param tags La lista de etiquetas asociadas (tablas "hijas").
 */
data class MoodEntryWithTags(
    @Embedded
    val entry: MoodEntry,

    @Relation(
        parentColumn = "id", // Columna ID de MoodEntry
        entityColumn = "id",   // Columna ID de ActivityTag
        associateBy = Junction(
            value = EntryTagCrossRef::class,
            parentColumn = "moodEntryId", // Columna en CrossRef que apunta a MoodEntry
            entityColumn = "activityTagId" // Columna en CrossRef que apunta a ActivityTag
        )
    )
    val tags: List<ActivityTag>
)
