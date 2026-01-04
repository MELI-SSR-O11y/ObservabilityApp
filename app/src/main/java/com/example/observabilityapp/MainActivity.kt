package com.example.observabilityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.observabilityapp.screens.FavoritesScreen
import com.example.observabilityapp.screens.InfoScreen
import com.example.observabilityapp.screens.ProfileScreen
import com.example.observabilityapp.screens.UsersScreen
import com.example.observabilityapp.ui.theme.ObservabilityAppTheme
import com.example.observabilityapp.utils.AppDestinations
import com.example.presentation.main.ContractObservabilityApi
import com.example.presentation.main.MainActions
import org.koin.compose.koinInject

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
fun ObservabilityAppApp(api: ContractObservabilityApi = koinInject()) {
  var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
  val state by api.state.collectAsStateWithLifecycle()

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
    Scaffold(modifier = Modifier.fillMaxSize(), floatingActionButton = {
      if(!state.isSync) {
        ExtendedFloatingActionButton(
          onClick = { api.onEvent(MainActions.SyncToRemote) },
          icon = {
            Icon(
              painter = painterResource(R.drawable.outline_cloud_sync_24),
              contentDescription = stringResource(R.string.sync_to_remote)
            ) },
          text = { Text(text = stringResource(R.string.sync_to_remote)) },
        )
      }
    }) { innerPadding ->
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