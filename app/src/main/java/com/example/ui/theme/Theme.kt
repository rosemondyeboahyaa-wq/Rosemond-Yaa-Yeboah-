package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme =
  lightColorScheme(
    primary = ElectricTeal,
    secondary = NeonPurple,
    tertiary = ActivePink,
    background = SlateDarkBg,
    surface = SlateSurface,
    surfaceVariant = SlateSurfaceVariant,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = SlateBorder,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false, // Use bright light mode theme for professional cream/ivory aesthetic
  dynamicColor: Boolean = false, // Disable dynamic colors to keep brand colors intact
  content: @Composable () -> Unit,
) {
  val colorScheme = LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
