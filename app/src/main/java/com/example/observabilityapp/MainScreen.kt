package com.example.observabilityapp

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.models.TimeFilter
import com.example.domain.util.EIncidentSeverity
import com.example.observabilityapp.components.FilterDropDown
import com.example.observabilityapp.components.IncidentTimeSeriesChart
import com.example.observabilityapp.components.SeverityPieChart
import com.example.presentation.main.ContractObservabilityApi
import com.example.presentation.main.MainActions
import org.koin.compose.koinInject

@Composable
fun MainScreen(
  modifier : Modifier = Modifier,
  innerPaddingValues : PaddingValues,
  sdk : ContractObservabilityApi = koinInject(),
) {
  val state by sdk.state.collectAsStateWithLifecycle()
  val onEvent = sdk::onEvent
  val configuration = LocalConfiguration.current
  val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(innerPaddingValues)
      .padding(16.dp)
      .verticalScroll(rememberScrollState())
  ) {
    if(state.isLoading) {
      LinearProgressIndicator(modifier = Modifier
        .height(4.dp)
        .fillMaxWidth())
    }

    if(isLandscape) {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterDropDown(
          label = "Screen",
          items = state.screens,
          selectedItem = state.screens.find { it.id == state.activeFilter.screenId },
          onItemSelected = { onEvent(MainActions.FilterByScreen(it?.id)) },
          itemToString = { it.name },
          modifier = Modifier.weight(1f)
        )
        FilterDropDown(
          label = "Severity",
          items = EIncidentSeverity.entries.toList(),
          selectedItem = state.activeFilter.severity,
          onItemSelected = { onEvent(MainActions.FilterBySeverity(it)) },
          itemToString = { it.name },
          modifier = Modifier.weight(1f)
        )
        FilterDropDown(
          label = "Time",
          items = TimeFilter.allFilters(),
          selectedItem = state.activeFilter.timeFilter,
          onItemSelected = { onEvent(MainActions.FilterByTime(it ?: TimeFilter.None)) },
          itemToString = { it.displayName },
          modifier = Modifier.weight(1f)
        )
      }
    } else {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterDropDown(
          label = "Screen",
          items = state.screens,
          selectedItem = state.screens.find { it.id == state.activeFilter.screenId },
          onItemSelected = { onEvent(MainActions.FilterByScreen(it?.id)) },
          itemToString = { it.name },
          modifier = Modifier.weight(1f)
        )
        FilterDropDown(
          label = "Severity",
          items = EIncidentSeverity.entries.toList(),
          selectedItem = state.activeFilter.severity,
          onItemSelected = { onEvent(MainActions.FilterBySeverity(it)) },
          itemToString = { it.name },
          modifier = Modifier.weight(1f)
        )
      }
      FilterDropDown(
        label = "Time",
        items = TimeFilter.allFilters(),
        selectedItem = state.activeFilter.timeFilter,
        onItemSelected = { onEvent(MainActions.FilterByTime(it ?: TimeFilter.None)) },
        itemToString = { it.displayName },
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(text = "Screens: ${state.screensQuantity}")
    Text(text = "Incidents: ${state.incidentsQuantity}")
    Spacer(modifier = Modifier.height(16.dp))

    if(isLandscape) {
      Row(
        Modifier
          .fillMaxWidth()
          .height(300.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Box(modifier = Modifier.weight(1f)) { SeverityPieChart(state) }
        Box(modifier = Modifier.weight(1f)) { IncidentTimeSeriesChart(state) }
      }
    } else {
      SeverityPieChart(state)
      Spacer(modifier = Modifier.height(24.dp))
      IncidentTimeSeriesChart(state)
    }

    Spacer(modifier = Modifier.height(24.dp))

  }
}