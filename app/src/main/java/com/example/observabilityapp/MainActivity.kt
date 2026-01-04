package com.example.observabilityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.observabilityapp.components.ObservabilityApp
import com.example.observabilityapp.ui.theme.ObservabilityAppTheme

class MainActivity: ComponentActivity() {
  override fun onCreate(savedInstanceState : Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      ObservabilityAppTheme {
        ObservabilityApp()
      }
    }
  }
}