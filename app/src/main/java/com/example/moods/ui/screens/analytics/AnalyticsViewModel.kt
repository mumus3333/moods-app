package com.example.moods.ui.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moods.data.model.MoodEntryWithTags
import com.example.moods.data.repository.MoodRepository
import com.patrykandpatryk.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatryk.vico.core.entry.FloatEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import javax.inject.Inject

/**
 * Datos para la gráfica de pastel.
 * @param label La etiqueta (ej: "Bajo", "Neutral", "Alto").
 * @param count El número de registros en esta categoría.
 */
data class MoodState(val label: String, val count: Int)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    repository: MoodRepository
) : ViewModel() {

    // --- Datos de Gráfica de Barras (Últimos 7 días) ---

    // Un productor de modelo que Vico puede consumir
    val barChartEntryModelProducer = ChartEntryModelProducer()

    /**
     * Un flujo que calcula los promedios de los últimos 7 días.
     * Se activa cuando la UI lo recolecta.
     */
    val barChartData: StateFlow<Unit> = repository.getAllEntriesWithTags()
        .map { allEntries ->
            val dailyAverages = calculateLast7DaysAverage(allEntries)
            // Actualiza el productor de Vico con los nuevos datos
            barChartEntryModelProducer.setEntries(dailyAverages)

            // [CORRECCIÓN] Añadimos 'Unit' para que coincida con el tipo de StateFlow<Unit>
            Unit
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Unit // Valor inicial, no importa
        )

    /**
     * Un flujo que calcula la distribución de estados de ánimo para la gráfica de pastel.
     */
    val pieChartData: StateFlow<List<MoodState>> = repository.getAllEntriesWithTags()
        .map { allEntries ->
            calculateMoodStateDistribution(allEntries)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList() // Valor inicial
        )

    // --- Funciones de Ayuda (Helpers) ---

    /**
     * Calcula la distribución de estados (Bajo/Neutral/Alto) del total de registros.
     */
    private fun calculateMoodStateDistribution(entries: List<MoodEntryWithTags>): List<MoodState> {
        // Contamos cuántos registros caen en cada categoría
        val lowCount = entries.count { it.entry.moodRating <= 2 }
        val neutralCount = entries.count { it.entry.moodRating == 3 }
        val highCount = entries.count { it.entry.moodRating >= 4 }

        // Creamos la lista de datos para el PieChart
        return listOf(
            MoodState("Bajo (1-2)", lowCount),
            MoodState("Neutral (3)", neutralCount),
            MoodState("Alto (4-5)", highCount)
        ).filter { it.count > 0 } // Solo mostramos categorías que tengan datos
    }

    /**
     * Calcula el promedio de ánimo para cada uno de los últimos 7 días.
     */
    private fun calculateLast7DaysAverage(entries: List<MoodEntryWithTags>): List<FloatEntry> {
        val dailyAverages = mutableMapOf<Long, Pair<Int, Int>>() // Map<Día, Pair<Suma de Ratings, Conteo>>
        val calendar = Calendar.getInstance()

        // 1. Obtener los últimos 7 días (incluyendo hoy)
        val last7DaysTimestamps = (0..6).map { dayAgo ->
            getStartOfDayMillis(calendar, daysAgo = dayAgo)
        }.reversed() // Ordenar de más antiguo a más reciente

        // 2. Inicializar el mapa con los 7 días
        last7DaysTimestamps.forEach { dayTimestamp ->
            dailyAverages[dayTimestamp] = Pair(0, 0)
        }

        // 3. Procesar las entradas y agruparlas por día
        entries.forEach { entry ->
            val entryDay = getStartOfDayMillis(calendar, entry.entry.timestamp)
            if (dailyAverages.containsKey(entryDay)) {
                val (currentSum, currentCount) = dailyAverages[entryDay]!!
                dailyAverages[entryDay] = Pair(currentSum + entry.entry.moodRating, currentCount + 1)
            }
        }

        // 4. Calcular los promedios y convertir a FloatEntry para Vico
        var xIndex = 0f
        return last7DaysTimestamps.map { dayTimestamp ->
            val (sum, count) = dailyAverages[dayTimestamp]!!
            val average = if (count > 0) sum.toFloat() / count.toFloat() else 0f
            FloatEntry(x = xIndex++, y = average)
        }
    }

    /**
     * Devuelve el timestamp (medianoche) para un día específico.
     */
    private fun getStartOfDayMillis(calendar: Calendar, timestamp: Long? = null, daysAgo: Int = 0): Long {
        calendar.timeInMillis = timestamp ?: System.currentTimeMillis()
        if (daysAgo > 0) {
            calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}

