package com.example.moods.ui.screens.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moods.data.model.ActivityTag
import com.example.moods.data.model.MoodEntry
// Ya no importamos 'TagType'
import com.example.moods.data.repository.MoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val repository: MoodRepository
) : ViewModel() {

    // --- Flujos de datos para la UI ---

    // [CORREGIDO] Pasamos el String "ACTIVITY"
    val activities: Flow<List<ActivityTag>> =
        repository.getTagsByType("ACTIVITY")

    // [CORREGIDO] Pasamos el String "LOCATION"
    val locations: Flow<List<ActivityTag>> =
        repository.getTagsByType("LOCATION")

    // [CORREGIDO] Pasamos el String "EVENT"
    val events: Flow<List<ActivityTag>> =
        repository.getTagsByType("EVENT")

    // --- Acciones del Usuario ---

    fun saveMoodEntry(
        rating: Int,
        notes: String,
        selectedTags: List<ActivityTag>
    ) {
        viewModelScope.launch {
            val entry = MoodEntry(
                timestamp = System.currentTimeMillis(),
                moodRating = rating,
                notes = notes.takeIf { it.isNotBlank() }
            )
            repository.addNewEntry(entry, selectedTags)
        }
    }
}

