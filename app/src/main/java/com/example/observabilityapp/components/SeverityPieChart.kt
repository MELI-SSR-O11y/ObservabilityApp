package com.example.observabilityapp.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.presentation.main.MainState

@Composable
fun SeverityPieChart(state: MainState, modifier: Modifier = Modifier) {
  val severityData = listOfNotNull(
    if (state.debugSeverityQuantity > 0) PieChartData("Debug", state.debugSeverityQuantity, Color.Gray) else null,
    if (state.infoSeverityQuantity > 0) PieChartData("Info", state.infoSeverityQuantity, Color.Blue) else null,
    if (state.warningSeverityQuantity > 0) PieChartData("Warning", state.warningSeverityQuantity, Color(0xFFFFA500)) else null, // Naranja
    if (state.errorSeverityQuantity > 0) PieChartData("Error", state.errorSeverityQuantity, Color.Red) else null,
    if (state.criticalSeverityQuantity > 0) PieChartData("Critical", state.criticalSeverityQuantity, Color(0xFF8B0000)) else null // Rojo Oscuro
  )

  if (severityData.isEmpty()) {
    Text("No incident data for pie chart.", modifier = modifier.padding(vertical = 32.dp))
    return
  }

  Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
    Text("Incident Severity Distribution", style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(16.dp))

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(150.dp)
          .weight(1f),
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val total = severityData.sumOf { it.count }.toFloat()
          var startAngle = -90f
          severityData.forEach { data ->
            val sweepAngle = (data.count / total) * 360f
            drawArc(
              color = data.color,
              startAngle = startAngle,
              sweepAngle = sweepAngle,
              useCenter = true
            )
            startAngle += sweepAngle
          }
        }
      }
      Spacer(modifier = Modifier.width(16.dp))
      Column(modifier = Modifier.weight(1f)) {
        severityData.forEach { data ->
          LegendItem(label = "${data.label} (${data.count})", color = data.color)
        }
      }
    }
  }
}

private data class PieChartData(val label: String, val count: Int, val color: Color)