package com.example.observabilityapp.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.observabilityapp.components.IncidentGeneratorButtons
import com.example.observabilityapp.utils.AppDestinations
import com.example.presentation.main.ContractObservabilityApi
import com.example.presentation.main.MainActions
import org.koin.compose.koinInject

@Composable
fun UsersScreen(innerPaddingValues : PaddingValues, api: ContractObservabilityApi = koinInject()) {
  val onEvent = api::onEvent
  val screenName by remember { mutableStateOf(AppDestinations.USERS.label) }

  LaunchedEffect(Unit) {
    onEvent(MainActions.InsertScreen(screenName))
  }
  IncidentGeneratorButtons(screenName, onEvent, innerPaddingValues)
}