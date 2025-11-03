package com.example.moods.ui.screens.analytics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moods.ui.components.PieChart
import com.patrykandpatryk.vico.compose.chart.Chart
import com.patrykandpatryk.vico.compose.chart.column.columnChart
import com.patrykandpatryk.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatryk.vico.compose.axis.vertical.startAxis
import com.patrykandpatryk.vico.core.axis.AxisPosition
import com.patrykandpatryk.vico.core.axis.formatter.AxisValueFormatter

/**
 * Pantalla que muestra las gráficas de análisis.
 * [ACTUALIZADA] Ahora incluye Gráfica de Barras (Promedios) y Gráfica de Pastel (Distribución).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    // Recolectar los flujos (flows) del ViewModel como estado
    val barChartProducer = viewModel.barChartEntryModelProducer
    val pieChartData by viewModel.pieChartData.collectAsState()

    // Trigger para que el viewModel empiece a calcular la gráfica de barras
    viewModel.barChartData.collectAsState()

    // Formateador para los ejes de la gráfica de barras (Día 1, Día 2, etc.)
    val axisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
        "Día ${value.toInt() + 1}"
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Análisis de Ánimo") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- 1. Gráfica de Barras (Promedio últimos 7 días) ---
            Text(
                "Promedio de Ánimo (Últimos 7 Días)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))

            // Usamos una Card para darle un fondo y elevación
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                if (barChartProducer.getModel()?.entries.orEmpty().isNotEmpty()) {
                    Chart(
                        chart = columnChart(),
                        chartModelProducer = barChartProducer,
                        startAxis = startAxis( // Eje Y (Izquierda)
                            title = "Promedio (1-5)",
                            maxLabelCount = 5
                        ),
                        bottomAxis = bottomAxis( // Eje X (Abajo)
                            title = "Días (Hoy = Día 7)",
                            valueFormatter = axisValueFormatter
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    Text("Cargando datos...", modifier = Modifier.padding(16.dp))
                }
            }

            Spacer(Modifier.height(32.dp))

            // --- 2. Gráfica de Pastel (Distribución de Estados) ---
            Text(
                "Distribución de Estados de Ánimo",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                // Llamamos a nuestro Composable personalizado
                PieChart(
                    data = pieChartData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    size = 180.dp // Tamaño de la gráfica
                )
            }
        }
    }
}

