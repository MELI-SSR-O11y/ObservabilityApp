package com.example.observabilityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.example.observabilityapp.screens.FavoritesScreen
import com.example.observabilityapp.screens.InfoScreen
import com.example.observabilityapp.screens.ProfileScreen
import com.example.observabilityapp.screens.UsersScreen
import com.example.observabilityapp.ui.theme.ObservabilityAppTheme
import com.example.observabilityapp.utils.AppDestinations

class MainActivity: ComponentActivity() {
  override fun onCreate(savedInstanceState : Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      ObservabilityAppTheme {
        ObservabilityAppApp()
      }
    }
  }
}

@PreviewScreenSizes
@Composable
fun ObservabilityAppApp() {
  var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

  NavigationSuiteScaffold(
    navigationSuiteItems = {
      AppDestinations.entries.forEach {
        item(
          icon = {
          Icon(
            it.icon, contentDescription = it.label
          )
        },
          label = { Text(it.label) },
          selected = it == currentDestination,
          onClick = { currentDestination = it })
      }
    }) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
      when (currentDestination) {
        AppDestinations.HOME -> MainScreen(innerPaddingValues = innerPadding)
        AppDestinations.FAVORITES -> FavoritesScreen(innerPaddingValues = innerPadding)
        AppDestinations.PROFILE -> ProfileScreen(innerPaddingValues = innerPadding)
        AppDestinations.USERS -> UsersScreen(innerPaddingValues = innerPadding)
        AppDestinations.INFO -> InfoScreen(innerPaddingValues = innerPadding)
      }
    }
  }
}