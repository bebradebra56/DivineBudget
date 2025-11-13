package com.divinebudget.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Gold,
    onPrimary = Color(0xFF1A1A1A),
    primaryContainer = LightGold,
    onPrimaryContainer = Color(0xFF1A1A1A),
    secondary = Turquoise,
    onSecondary = Color.White,
    secondaryContainer = LightTurquoise,
    onSecondaryContainer = Color(0xFF1A1A1A),
    tertiary = LapisBlue,
    onTertiary = Color.White,
    tertiaryContainer = LapisBlue,
    onTertiaryContainer = Color.White,
    error = ExpenseRed,
    onError = Color.White,
    errorContainer = ExpenseRed,
    onErrorContainer = Color.White,
    background = Color(0xFFFFFBF0),
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFFFFDF5),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFE8DCC8),
    onSurfaceVariant = Color(0xFF1A1A1A),
    outline = Gold,
    outlineVariant = DarkGold,
    scrim = BlackObsidian,
    inverseSurface = Color(0xFF2A2520),
    inverseOnSurface = Color(0xFFF5F5F5),
    inversePrimary = LightGold,
    surfaceTint = Gold
)

private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = Color(0xFF1A1A1A),
    primaryContainer = DarkGold,
    onPrimaryContainer = Color(0xFFF5F5F5),
    secondary = Turquoise,
    onSecondary = Color(0xFF1A1A1A),
    secondaryContainer = DarkTurquoise,
    onSecondaryContainer = Color(0xFFF5F5F5),
    tertiary = LapisBlue,
    onTertiary = Color.White,
    tertiaryContainer = LapisBlue,
    onTertiaryContainer = Color.White,
    error = Color(0xFFCF6679),
    onError = Color(0xFF1A1A1A),
    errorContainer = Color(0xFFB00020),
    onErrorContainer = Color.White,
    background = Color(0xFF1A1612),
    onBackground = Color(0xFFF5F5F5),
    surface = Color(0xFF2A2520),
    onSurface = Color(0xFFF5F5F5),
    surfaceVariant = Color(0xFF3A342F),
    onSurfaceVariant = Color(0xFFE8DCC8),
    outline = Gold,
    outlineVariant = DarkGold,
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFF5F5F5),
    inverseOnSurface = Color(0xFF1A1A1A),
    inversePrimary = DarkGold,
    surfaceTint = Gold
)

@Composable
fun DivineBudgetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

