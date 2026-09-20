package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val NexusDarkColorScheme = darkColorScheme(
  primary = NexusCyan,
  onPrimary = NexusVoid,
  primaryContainer = NexusSurfaceVariant,
  onPrimaryContainer = NexusCyan,
  secondary = NexusBlue,
  onSecondary = NexusTextPrimary,
  secondaryContainer = NexusSurfaceVariant,
  onSecondaryContainer = NexusBlue,
  tertiary = NexusViolet,
  onTertiary = NexusTextPrimary,
  background = NexusVoid,
  onBackground = NexusTextPrimary,
  surface = NexusSurface,
  onSurface = NexusTextPrimary,
  surfaceVariant = NexusSurfaceVariant,
  onSurfaceVariant = NexusTextSecondary,
  error = NexusCrimson,
  onError = NexusTextPrimary,
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = NexusDarkColorScheme,
    typography = Typography,
    content = content
  )
}

