package com.example.moods.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.moods.ui.screens.analytics.MoodState

/**
 * Colores base para la gráfica de pastel.
 * Usamos colores significativos: Rojo/Rosa para bajo, Ámbar para neutral, Verde para alto.
 */
val pieChartColors = listOf(
    Color(0xFFE57373), // Rojo claro (Bajo)
    Color(0xFFFFC107), // Ámbar (Neutral)
    Color(0xFF81C784)  // Verde claro (Alto)
)

/**
 * Composable personalizado para una gráfica de pastel simple con leyenda.
 *
 * @param modifier Modificador de Compose.
 * @param size El tamaño del lienzo (Canvas) de la gráfica.
 * @param data Lista de [MoodState] para mostrar.
 */
@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    size: Dp = 150.dp,
    data: List<MoodState>
) {
    val total = data.sumOf { it.count }.toFloat()
    if (total == 0f) {
        Text("No hay datos para la gráfica de pastel.", modifier = modifier.padding(16.dp))
        return
    }

    // Animación de entrada para la gráfica
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "PieChartAnimation"
    )
    LaunchedEffect(key1 = data) {
        animationPlayed = true
    }

    // Asignar colores a los datos
    val colors = pieChartColors.take(data.size)

    Row(
        modifier = modifier.padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- Gráfica de Pastel (Canvas) ---
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size)) {
                var startAngle = -90f // Empezar desde la parte superior (12 en punto)
                data.forEachIndexed { index, state ->
                    val proportion = state.count / total
                    val sweepAngle = 360f * proportion * animatedProgress // Aplicar animación

                    drawArc(
                        color = colors.getOrElse(index) { Color.Gray }, // Usar color o gris por defecto
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true, // Relleno (gráfica de pastel)
                        size = Size(width = size.toPx(), height = size.toPx())
                    )

                    // Actualizar el ángulo de inicio para el siguiente segmento
                    startAngle += (360f * proportion)
                }
            }
        }

        Spacer(Modifier.width(24.dp))

        // --- Leyenda ---
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            data.forEachIndexed { index, state ->
                val percentage = (state.count / total) * 100
                LegendItem(
                    color = colors.getOrElse(index) { Color.Gray },
                    label = state.label,
                    value = state.count,
                    percentage = percentage
                )
            }
        }
    }
}

/**
 * Un ítem de la leyenda para la gráfica de pastel.
 * Muestra el color, etiqueta, conteo y porcentaje.
 */
@Composable
private fun LegendItem(
    color: Color,
    label: String,
    value: Int,
    percentage: Float
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Cuadro de color
        Canvas(modifier = Modifier.size(12.dp)) {
            drawRect(color = color)
        }
        Spacer(Modifier.width(8.dp))
        // Texto descriptivo
        Text(
            text = "$label ($value) - ${String.format("%.1f", percentage)}%",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

