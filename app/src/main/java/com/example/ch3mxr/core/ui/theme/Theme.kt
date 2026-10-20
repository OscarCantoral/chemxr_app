package com.example.ch3mxr.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class ThemeMode { SYSTEM, LIGHT, DARK }

// Los componentes pueden consultar si el tema real es oscuro, aunque el usuario
// haya escogido DARK/LIGHT manualmente en vez de seguir el sistema.
val LocalChemIsDark = staticCompositionLocalOf { true }

@Composable
fun ChemXRTheme(
    mode: ThemeMode = ThemeMode.SYSTEM,
    accentColor: Color = ChemLime,
    content: @Composable () -> Unit
) {
    val isDark = when (mode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val scheme = if (isDark) {
        darkColorScheme(
            primary = accentColor,
            onPrimary = ChemDarkBackground,
            background = ChemDarkBackground,
            onBackground = Color.White,
            surface = ChemDarkSurface,
            onSurface = Color.White,
            onSurfaceVariant = Color(0xFFA4ADB8),
            outline = Color(0xFF6B7C89)
        )
    } else {
        lightColorScheme(
            primary = accentColor,
            onPrimary = ChemDarkBackground,
            background = ChemLightBackground,
            onBackground = ChemDarkBackground,
            surface = ChemLightSurface,
            onSurface = ChemDarkBackground,
            onSurfaceVariant = Color(0xFF637282),
            outline = Color(0xFF697785)
        )
    }

    CompositionLocalProvider(LocalChemIsDark provides isDark) {
        MaterialTheme(
            colorScheme = scheme,
            typography = ChemTypography,
            shapes = Shapes(
                extraSmall = ChemShapes.Component,
                small = ChemShapes.Component,
                medium = ChemShapes.Component,
                large = ChemShapes.Component,
                extraLarge = ChemShapes.Component
            ),
            content = content
        )
    }
}
