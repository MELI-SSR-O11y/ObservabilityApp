package com.example.observabilityapp.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.example.presentation.main.MainState
import java.time.Duration
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.Temporal

@Composable
fun IncidentTimeSeriesChart(state: MainState, modifier: Modifier = Modifier) {
  val filter = state.activeFilter
  val incidentsToDisplay = state.screens
    // Apply screen filter first if a screen is selected
    .filter { screen -> filter.screenId == null || screen.id == filter.screenId }
    // Flatten to a list of all incidents from the filtered screens
    .flatMap { it.incidentTrackers }
    // Then, filter these incidents by severity and time
    .filter { incident ->
      val severityMatch = filter.severity == null || incident.severity == filter.severity
      val timeLimit = filter.timeFilter.durationMillis.takeIf { it > 0 }?.let {
        System.currentTimeMillis() - it
      }
      val timeMatch = timeLimit == null || incident.timestamp >= timeLimit
      severityMatch && timeMatch
    }

  if (incidentsToDisplay.size < 2) {
    Text("Not enough data for time series chart with current filters.", modifier = modifier.padding(vertical = 32.dp))
    return
  }

  val timestamps = incidentsToDisplay.map { it.timestamp }
  val minTimestamp = timestamps.minOrNull() ?: return
  val maxTimestamp = timestamps.maxOrNull() ?: return
  val duration = Duration.ofMillis(maxTimestamp - minTimestamp)

  val (formatter, incidentsByTime) = when {
    duration.toMinutes() < 120 -> DateTimeFormatter.ofPattern("HH:mm") to incidentsToDisplay.groupBy { Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime().truncatedTo(ChronoUnit.MINUTES) }.mapValues { it.value.size }.toSortedMap()
    duration.toHours() < 48 -> DateTimeFormatter.ofPattern("d / HH:00") to incidentsToDisplay.groupBy { Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime().truncatedTo(ChronoUnit.HOURS) }.mapValues { it.value.size }.toSortedMap()
    duration.toDays() < 60 -> DateTimeFormatter.ofPattern("MMM d") to incidentsToDisplay.groupBy { Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate() }.mapValues { it.value.size }.toSortedMap()
    else -> DateTimeFormatter.ofPattern("MMM yyyy") to incidentsToDisplay.groupBy { YearMonth.from(Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()) }.mapValues { it.value.size }.toSortedMap()
  }

  if (incidentsByTime.size < 2) {
    Text("Data points are not distinct enough to draw a line chart.", modifier = modifier.padding(vertical = 32.dp))
    return
  }

  val dataPoints = incidentsByTime.entries.toList()
  val maxIncidents = incidentsByTime.values.maxOrNull() ?: 1
  val lineChartColor = MaterialTheme.colorScheme.primary
  val axisColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
  val yAxisLabelWidth = 40.dp

  Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
    Text("Incidents Over Time", style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(16.dp))

    Row(modifier = Modifier.fillMaxWidth().height(200.dp)) {
      // Y-Axis Labels
      Column(
        modifier = Modifier.width(yAxisLabelWidth).fillMaxHeight(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Text(maxIncidents.toString(), style = MaterialTheme.typography.bodySmall)
        Text("0", style = MaterialTheme.typography.bodySmall)
      }

      // Chart Canvas
      Canvas(modifier = Modifier.fillMaxSize()) {
        val chartWidth = size.width
        val chartHeight = size.height

        // Draw X and Y axes
        drawLine(color = axisColor, start = Offset(0f, chartHeight), end = Offset(chartWidth, chartHeight), strokeWidth = 2f)
        drawLine(color = axisColor, start = Offset(0f, 0f), end = Offset(0f, chartHeight), strokeWidth = 2f)

        val xStep = if (dataPoints.size > 1) chartWidth / (dataPoints.size - 1) else 0f
        val yRatio = if (maxIncidents > 0) chartHeight / maxIncidents else 0f

        // Draw the line and points
        for (i in 0 until dataPoints.size - 1) {
          val p1 = dataPoints[i]
          val p2 = dataPoints[i + 1]
          val x1 = i * xStep
          val y1 = chartHeight - (p1.value * yRatio)
          val x2 = (i + 1) * xStep
          val y2 = chartHeight - (p2.value * yRatio)
          drawLine(color = lineChartColor, start = Offset(x1, y1), end = Offset(x2, y2), strokeWidth = 4f)
        }
        dataPoints.forEachIndexed { index, entry ->
          val x = index * xStep
          val y = chartHeight - (entry.value * yRatio)
          drawCircle(color = lineChartColor, radius = 8f, center = Offset(x, y))
        }
      }
    }

    // X-Axis Labels
    Row(
      modifier = Modifier.fillMaxWidth().padding(start = yAxisLabelWidth),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      val firstLabel = formatter.format(dataPoints.first().key as Temporal)
      val lastLabel = formatter.format(dataPoints.last().key as Temporal)

      Text(firstLabel, style = MaterialTheme.typography.bodySmall)
      if (dataPoints.size > 2) {
        val middleIndex = dataPoints.size / 2
        val middleLabel = formatter.format(dataPoints[middleIndex].key as Temporal)
        Text(middleLabel, style = MaterialTheme.typography.bodySmall)
      }
      Text(lastLabel, style = MaterialTheme.typography.bodySmall)
    }
  }
}