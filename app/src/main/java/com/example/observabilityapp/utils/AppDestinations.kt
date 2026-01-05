package com.example.observabilityapp.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector


enum class AppDestinations(
  val label : String,
  val icon : ImageVector,
) {
  HOME("Dashboard", Icons.Default.Home),
  FAVORITES("Favoritos", Icons.Default.Favorite),
  PROFILE("Perfil", Icons.Default.AccountBox),
  USERS("Usuarios", Icons.Default.AccountCircle),
  INFO("Info", Icons.Default.Info),
}