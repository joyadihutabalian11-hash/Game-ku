package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = JoyPurple,
    onPrimary = Color.White,
    primaryContainer = JoyPurpleLight,
    onPrimaryContainer = JoyPurpleDark,
    secondary = JoyCyan,
    onSecondary = Color.Black,
    tertiary = JoyYellow,
    onTertiary = Color.Black,
    background = JoyBackground,
    onBackground = JoyOnBackground,
    surface = JoySurface,
    onSurface = JoyOnBackground
)

private val DarkColorScheme = darkColorScheme(
    primary = JoyPurpleLight,
    onPrimary = JoyPurpleDark,
    secondary = JoyCyan,
    onSecondary = Color.Black,
    tertiary = JoyYellow,
    onTertiary = Color.Black,
    background = Color(0xFF1A102F),
    onBackground = Color.White,
    surface = Color(0xFF261947),
    onSurface = Color.White
)

@Composable
fun JoyGamingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
