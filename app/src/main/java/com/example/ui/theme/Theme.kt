package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF0A84FF),        // Beautiful rich blue
    onPrimary = Color.White,
    secondary = Color(0xFF002244),      // Deep dark blue
    onSecondary = Color.White,
    tertiary = Color(0xFF00E5FF),       // Bright cyan/blue accent
    background = Color(0xFF000000),     // Absolute black for high definition
    onBackground = Color(0xFFFFFFFF),   // High contrast white text
    surface = Color(0xFF0B1426),        // Elegant dark blue shading surface
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF17253F),  // Soft shaded boundary box
    onSurfaceVariant = Color(0xFF94A3B8) // Muted slate grey
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1E88E5),        // Clean professional blue
    onPrimary = Color.White,
    secondary = Color(0xFFE3F2FD),      // Soft blue shaded boxes
    onSecondary = Color(0xFF0D47A1),
    tertiary = Color(0xFF26A69A),
    background = Color(0xFFF8FAFC),     // Clean minimal white-slate
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),        // White card surfaces
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF64748B)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Disable system dynamic coloring to guarantee taha's curated signature palette
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
