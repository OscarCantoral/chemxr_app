package com.example.ch3mxr.core.ui.theme

import androidx.compose.ui.graphics.Color

// Marca y superficies: una sola fuente para los temas.
val ChemLime = Color(0xFFC4F46D)
val ChemDarkBackground = Color(0xFF121C26)
val ChemDarkSurface = Color(0xFF192633)
val ChemLightBackground = Color(0xFFF8FAFC)
val ChemLightSurface = Color.White

// Colores complementarios de los mockups. Reutilizables en todas las vistas.
object ChemPalette {
    val AccentBlue = Color(0xFF90C8FA)
    val AccentPurple = Color(0xFFD2A3F2)
    val AccentOrange = Color(0xFFFFC078)
    val DarkMuted = Color(0xFFA4ADB8)
    val DarkOutline = Color(0xFF6B7C89)
    val LightMuted = Color(0xFF637282)
    val LightOutline = Color(0xFF697785)
    val FeaturedCourseBackground = Color(0xFF20372F)
    val OceanTileBackground = Color(0xFF244053)
    val ScienceTileBackground = Color(0xFF263F33)
    val StrategyTileBackground = Color(0xFF283E35)
    val TechnologyTileBackground = Color(0xFF293954)
    val TutorTileBackground = Color(0xFF293D33)
    val ScienceAlternateBackground = Color(0xFF293E35)
    val NotificationTileBackground = Color(0xFF2A4035)
    val AgendaChipBackground = Color(0xFF2C4030)
    val CommunityTileBackground = Color(0xFF2C493B)
    val MusicTileBackground = Color(0xFF45354A)
    val MusicAlternateBackground = Color(0xFF46244F)
    val OceanTileForeground = Color(0xFF9CDAE6)
    val TechnologyTileForeground = Color(0xFFB2D9FF)
    val FeaturedTextMuted = Color(0xFFCAD9D1)
    val StrategyTileForeground = Color(0xFFCBF5A9)
    val ScienceTileForeground = Color(0xFFCDF1A9)
    val MusicHighlight = Color(0xFFD3B2DB)
    val FeaturedText = Color(0xFFD4E2DA)
    val MusicTileForeground = Color(0xFFEDC7F4)
}

enum class ChemAccent(val title: String, val color: Color) {
    LIME("Verde", ChemLime),
    BLUE("Azul", ChemPalette.AccentBlue),
    PURPLE("Lila", ChemPalette.AccentPurple),
    ORANGE("Ámbar", ChemPalette.AccentOrange)
}
