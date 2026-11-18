
package com.smart.expense.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// =========================================================
// STARLING BANK × WEALTHSIMPLE BENTO NEOBANK THEME
// Deep Oceanic Pine Dark | Crisp Porcelain Bento Light
// =========================================================

private val DarkColorScheme = darkColorScheme(
    // Primary: Starling Mint Electric (matches Starling neon active indicators)
    primary = StarlingMintAccent,
    onPrimary = Color(0xFF021B1F),
    primaryContainer = Color(0xFF0B4E5A),
    onPrimaryContainer = StarlingMintAccent,

    // Secondary: Starling Sea Glass Teal
    secondary = StarlingPineLight,
    onSecondary = Color.White,
    secondaryContainer = StarlingPineMedium,
    onSecondaryContainer = Color(0xFFCCF9F4),

    // Backgrounds: Starling Deep Abyssal Slate
    background = StarlingDarkBg,
    onBackground = StarlingDarkTextPrimary,

    // Surface (Cards): Starling Dark Card
    surface = StarlingDarkCard,
    onSurface = StarlingDarkTextPrimary,
    surfaceVariant = StarlingDarkCardSurface,
    onSurfaceVariant = StarlingDarkTextSecondary,

    // Outline: Hairline teal-slate border
    outline = StarlingDarkBorder,

    // Error
    error = Color(0xFFF87171),
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    // Primary: Starling Deep Oceanic Pine (Starling Brand)
    primary = StarlingPinePrimary,
    onPrimary = Color.White,
    primaryContainer = StarlingPineSubtle,
    onPrimaryContainer = StarlingPinePrimary,

    // Secondary: Starling Oceanic Teal
    secondary = StarlingPineMedium,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCF0F5),
    onSecondaryContainer = StarlingPineMedium,

    // Backgrounds: Wealthsimple Crisp Porcelain
    background = WealthsimpleLightBg,
    onBackground = WealthsimpleLightTextPrimary,

    // Surface (Cards): Pure White Bento Tiles
    surface = WealthsimpleLightCard,
    onSurface = WealthsimpleLightTextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = WealthsimpleLightTextSecondary,

    // Outline: Subtle hairline Slate 200
    outline = WealthsimpleLightBorder,

    // Error
    error = StarlingRedExpense,
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
