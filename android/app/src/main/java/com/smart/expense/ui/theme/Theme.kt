package com.smart.expense.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// =========================================================================
// PLAYFUL FINTECH × NEO-TACTILE × EDITORIAL MINIMALISM
// Warm Paper Canvas | Forest Ink Type | Electric Lime Tactile Highlights
// =========================================================================

private val DarkColorScheme = darkColorScheme(
    primary = PaletteElectricLime,
    onPrimary = PaletteForestInk,
    primaryContainer = DarkPaperSurface,
    onPrimaryContainer = PaletteElectricLime,

    secondary = PaletteLavender,
    onSecondary = PaletteForestInk,
    secondaryContainer = Color(0xFF2C2F3D),
    onSecondaryContainer = PaletteLavender,

    tertiary = PaletteCoralPop,
    onTertiary = PaletteForestInk,

    background = DarkPaperCanvas,
    onBackground = Color(0xFFF7F5ED),

    surface = DarkPaperCard,
    onSurface = Color(0xFFF7F5ED),
    surfaceVariant = DarkPaperSurface,
    onSurfaceVariant = Color(0xFFA6ABA0),

    outline = DarkPaperBorder,

    error = PaletteCoralPop,
    onError = PaletteForestInk
)

private val LightColorScheme = lightColorScheme(
    primary = PaletteForestInk,
    onPrimary = PaletteWarmPaper,
    primaryContainer = PaletteElectricLime,
    onPrimaryContainer = PaletteForestInk,

    secondary = PaletteForestInk,
    onSecondary = PaletteWarmPaper,
    secondaryContainer = PaletteMintMist,
    onSecondaryContainer = PaletteForestInk,

    tertiary = PaletteLavender,
    onTertiary = PaletteForestInk,

    background = PaletteWarmPaper,
    onBackground = PaletteForestInk,

    surface = PaletteWhite,
    onSurface = PaletteForestInk,
    surfaceVariant = Color(0xFFEFECE1),
    onSurfaceVariant = PaletteMutedText,

    outline = PaletteBorder,

    error = PaletteCoralPop,
    onError = PaletteWhite
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
