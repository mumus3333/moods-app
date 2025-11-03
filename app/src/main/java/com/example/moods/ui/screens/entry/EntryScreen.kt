package com.example.moods.ui.screens.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moods.data.model.ActivityTag
// [IMPORTANTE] Ya no importamos 'TagType' para evitar conflictos

/**
 * Pantalla principal para "Registrar" un nuevo estado de ánimo.
 * Es un formulario completo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryScreen(
    viewModel: EntryViewModel = hiltViewModel()
) {
    // --- Estados de la UI ---
    var rating by remember { mutableFloatStateOf(3f) } // Calificación (1-5)
    var notes by remember { mutableStateOf("") } // Notas
    val selectedTags = remember { mutableStateListOf<ActivityTag>() } // Etiquetas seleccionadas

    // --- Recolección de Flujos (Flows) ---
    // (El ViewModel ya pasa los Strings correctos al repositorio)
    val activities by viewModel.activities.collectAsState(initial = emptyList())
    val locations by viewModel.locations.collectAsState(initial = emptyList())
    val events by viewModel.events.collectAsState(initial = emptyList())

    // --- Lógica de UI ---
    val onSaveClick: () -> Unit = {
        // Llamar al ViewModel para guardar
        viewModel.saveMoodEntry(rating.toInt(), notes.trim(), selectedTags.toList())

        // Limpiar formulario
        rating = 3f
        notes = ""
        selectedTags.clear()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("¿Cómo te sientes hoy?") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Permite scroll si el contenido crece
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Slider de Calificación
            Text(
                text = "Calificación: ${rating.toInt()}",
                style = MaterialTheme.typography.titleMedium
            )
            Slider(
                value = rating,
                onValueChange = { rating = it },
                valueRange = 1f..5f,
                steps = 3, // 4 pasos (1, 2, 3, 4, 5)
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))

            // 2. Campo de Notas
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Añadir notas (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(Modifier.height(24.dp))

            // 3. Sección de Etiquetas (Actividades, Lugares, Eventos)
            // [CORRECCIÓN CLAVE]
            // Usamos los Strings puros ("ACTIVITY", "LOCATION", "EVENT")
            // Usamos los parámetros nombrados (name = ..., type = ...)
            // para el constructor de ActivityTag, ya que el 'id' es autogenerado.

            TagSection(
                title = "Actividades",
                tags = activities,
                tagType = "ACTIVITY",
                selectedTags = selectedTags,
                onTagClick = { toggleTag(selectedTags, it) },
                onNewTag = { tagName ->
                    selectedTags.add(ActivityTag(name = tagName, type = "ACTIVITY"))
                }
            )

            TagSection(
                title = "Lugares",
                tags = locations,
                tagType = "LOCATION",
                selectedTags = selectedTags,
                onTagClick = { toggleTag(selectedTags, it) },
                onNewTag = { tagName ->
                    selectedTags.add(ActivityTag(name = tagName, type = "LOCATION"))
                }
            )

            TagSection(
                title = "Eventos",
                tags = events,
                tagType = "EVENT",
                selectedTags = selectedTags,
                onTagClick = { toggleTag(selectedTags, it) },
                onNewTag = { tagName ->
                    selectedTags.add(ActivityTag(name = tagName, type = "EVENT"))
                }
            )

            Spacer(Modifier.height(32.dp))

            // 4. Botón de Guardar
            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("GUARDAR REGISTRO")
            }
        }
    }
}

/** Lógica simple para añadir/quitar un tag de la lista de seleccionados */
private fun toggleTag(list: MutableList<ActivityTag>, tag: ActivityTag) {
    // Comprobamos por nombre y tipo, ya que el ID puede ser 0 si es nuevo
    val existing = list.find { it.name == tag.name && it.type == tag.type }
    if (existing != null) {
        list.remove(existing)
    } else {
        list.add(tag)
    }
}


/**
 * Un Composable reutilizable para mostrar una sección de etiquetas
 * (con sugerencias y un campo para añadir nuevas).
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TagSection(
    title: String,
    tags: List<ActivityTag>,
    tagType: String, // [CORREGIDO] Acepta un String
    selectedTags: List<ActivityTag>,
    onTagClick: (ActivityTag) -> Unit,
    onNewTag: (String) -> Unit
) {
    var newTagText by remember { mutableStateOf("") }

    Column(Modifier.fillMaxWidth()) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        // Contenedor 'FlowRow' para que los chips fluyan a la siguiente línea
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Chips de sugerencias (tags existentes)
            tags.forEach { tag ->
                val isSelected = selectedTags.any { it.name == tag.name && it.type == tag.type }
                FilterChip(
                    selected = isSelected,
                    onClick = { onTagClick(tag) },
                    label = { Text(tag.name) }
                )
            }

            // Chips de etiquetas nuevas (que no están en sugerencias)
            // [CORREGIDO] Comparamos String con String (it.type == tagType)
            selectedTags.filter { it.type == tagType && tags.none { t -> t.name == it.name } }.forEach { tag ->
                FilterChip(
                    selected = true,
                    onClick = { onTagClick(tag) },
                    label = { Text(tag.name) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Campo para añadir nueva etiqueta
        OutlinedTextField(
            value = newTagText,
            onValueChange = { newTagText = it },
            label = { Text("Añadir $title nuevo...") },
            modifier = Modifier.fillMaxWidth()
        )
        // Botón para confirmar la nueva etiqueta
        ElevatedSuggestionChip(
            onClick = {
                val name = newTagText.trim()
                if(name.isNotBlank()) {
                    onNewTag(name) // Usamos .trim()
                    newTagText = ""
                }
            },
            label = { Text("Añadir \"$newTagText\"") },
            modifier = Modifier.padding(top = 8.dp),
            enabled = newTagText.isNotBlank() // Deshabilitado si está vacío
        )
        Spacer(Modifier.height(16.dp))
    }
}

