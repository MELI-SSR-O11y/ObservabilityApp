package com.example.observabilityapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.observabilityapp.utils.AppDestinations
import com.example.presentation.main.ContractObservabilityApi
import com.example.presentation.main.MainActions
import org.koin.compose.koinInject

@Composable
fun InfoScreen(innerPaddingValues : PaddingValues, api: ContractObservabilityApi = koinInject()) {
  val onEvent = api::onEvent

  LaunchedEffect(Unit) {
    onEvent(MainActions.InsertScreen(AppDestinations.INFO.label))
  }
  Row(
    modifier = Modifier
      .padding(innerPaddingValues)
      .fillMaxSize(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center
  ) {
    Text(text = "Info Screen")
  }
}