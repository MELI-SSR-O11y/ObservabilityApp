@file:OptIn(ExperimentalUuidApi::class)

package com.example.observabilityapp.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.domain.models.Metadata
import com.example.domain.util.EIncidentSeverity
import com.example.observabilityapp.R
import com.example.observabilityapp.data.provideFakeIncidentTracker
import com.example.presentation.main.MainActions
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun IncidentGeneratorButtons(
  screenName : String,
  onEvent : (MainActions) -> Unit,
  innerPaddingValues : PaddingValues,
) {

  Column(
    modifier = Modifier
      .padding(innerPaddingValues)
      .fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text("Pantalla de $screenName")
    Spacer(modifier = Modifier.height(16.dp))

    Button(onClick = {
      Log.d(screenName, "$screenName::DEBUG-Event")
      val metadata = listOf(
        Metadata(Uuid.random().toString(), "screen", screenName, false),
        Metadata(Uuid.random().toString(), "isBlocking", "false", false),
        Metadata(Uuid.random().toString(), "feature", "Observability App", false),
        Metadata(Uuid.random().toString(), "type", "Development", false),
      )
      onEvent(
        MainActions.InsertIncident(
          incident = provideFakeIncidentTracker(
            severity = EIncidentSeverity.DEBUG,
            message = "Debug Incident -> Location -> $screenName::DEBUG-Event",
            metadata = metadata
          ), screenName = screenName
        )
      )
    }) {
      Text(text = stringResource(id = R.string.add_debug_incidentt))
    }
    Button(onClick = {
      Log.i(screenName, "$screenName::INFO-Event")
      val metadata = listOf(
        Metadata(Uuid.random().toString(), "screen", screenName, false),
        Metadata(Uuid.random().toString(), "isBlocking", "false", false),
        Metadata(Uuid.random().toString(), "feature", "Observability App", false),
        Metadata(
          Uuid.random().toString(),
          "type",
          "X-API-KEY: For development only: " + Uuid.random(),
          false
        ),
      )
      onEvent(
        MainActions.InsertIncident(
          incident = provideFakeIncidentTracker(
            EIncidentSeverity.INFO, "Info Incident -> Location -> $screenName::INFO-Event", metadata
          ), screenName = screenName
        )
      )
    }) {
      Text(text = stringResource(id = R.string.add_info_incident))
    }
    Button(onClick = {
      Log.w(screenName, "$screenName::WARNING-Event")
      val metadata = listOf(
        Metadata(Uuid.random().toString(), "screen", screenName, false),
        Metadata(Uuid.random().toString(), "isBlocking", "false", false),
        Metadata(Uuid.random().toString(), "feature", "Observability App", false),
        Metadata(Uuid.random().toString(), "type", "Deprecated Libraries", false),
      )
      onEvent(
        MainActions.InsertIncident(
          incident = provideFakeIncidentTracker(
            EIncidentSeverity.WARNING,
            "WARNING Incident -> Location -> $screenName::WARNING-Event",
            metadata
          ), screenName = screenName
        )
      )
    }) {
      Text(text = stringResource(id = R.string.add_warning_incident))
    }
    Button(onClick = {
      Log.e(screenName, "$screenName::ERROR-Event")
      val metadata = listOf(
        Metadata(Uuid.random().toString(), "screen", screenName, false),
        Metadata(Uuid.random().toString(), "isBlocking", "true", false),
        Metadata(Uuid.random().toString(), "feature", "Observability App", false),
        Metadata(Uuid.random().toString(), "type", "ANR", false),
      )
      onEvent(
        MainActions.InsertIncident(
          incident = provideFakeIncidentTracker(
            EIncidentSeverity.ERROR,
            "Error Incident -> Location -> $screenName::ERROR-Event",
            metadata
          ), screenName = screenName
        )
      )
    }) {
      Text(text = stringResource(id = R.string.add_error_incident))
    }
    Button(onClick = {
      Log.wtf(screenName, "$screenName::CRITICAL-Event")
      val metadata = listOf(
        Metadata(Uuid.random().toString(), "screen", screenName, false),
        Metadata(Uuid.random().toString(), "isBlocking", "true", false),
        Metadata(Uuid.random().toString(), "feature", "Observability App", false),
        Metadata(Uuid.random().toString(), "type", "Memory Leak", false),
      )
      onEvent(
        MainActions.InsertIncident(
          incident = provideFakeIncidentTracker(
            EIncidentSeverity.CRITICAL,
            "Critical Incident -> Location -> $screenName::CRITICAL-Event",
            metadata
          ), screenName = screenName
        )
      )
    }) {
      Text(text = stringResource(id = R.string.add_critical_incident))
    }
  }
}