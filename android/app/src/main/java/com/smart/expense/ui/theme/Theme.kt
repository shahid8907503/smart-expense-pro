package com.smart.expense.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = FintechVioletLight,
    onPrimary = Color(0xFF0F1117),
    primaryContainer = FintechVioletContainer,
    onPrimaryContainer = FintechVioletLight,
    secondary = FintechCyanLight,
    onSecondary = Color.Black,
    secondaryContainer = FintechCyanContainer,
    onSecondaryContainer = FintechCyanLight,
    background = FintechMidnightBg,
    onBackground = FintechTextPrimary,
    surface = FintechCardBg,
    onSurface = FintechTextPrimary,
    surfaceVariant = FintechCardSurface,
    onSurfaceVariant = FintechTextSecondary,
    outline = FintechCardBorder,
    error = FintechCoralLight,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = FintechLightViolet,
    onPrimary = Color.White,
    primaryContainer = FintechLightVioletContainer,
    onPrimaryContainer = FintechLightViolet,
    secondary = FintechLightCyan,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = FintechLightCyan,
    background = FintechLightBg,
    onBackground = FintechLightTextPrimary,
    surface = FintechLightCardBg,
    onSurface = FintechLightTextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = FintechLightTextSecondary,
    outline = FintechLightCardBorder,
    error = FintechCoral,
    onError = Color.White
)

@Composable
fun SmartExpenseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
