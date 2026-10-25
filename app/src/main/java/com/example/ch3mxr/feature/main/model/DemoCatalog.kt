package com.example.ch3mxr.feature.main.model

import com.example.ch3mxr.core.ui.theme.ChemPalette

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Functions
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Science
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class DemoCourse(
    val title: String,
    val area: String,
    val subtitle: String,
    val progress: Float,
    val icon: ImageVector,
    val tileColor: Color,
    val iconColor: Color,
    val isPrivate: Boolean = false
)

// Datos FIJOS del mockup. No se almacenan, no usan API ni base de datos.
object DemoCatalog {
    val chemistry = DemoCourse("Química básica", "Ciencia", "Curso abierto", .40f, Icons.Outlined.Science, ChemPalette.ScienceTileBackground, ChemPalette.ScienceTileForeground)
    val programming = DemoCourse("Tu primer algoritmo", "Tecnología", "Curso abierto", 0f, Icons.Outlined.Code, ChemPalette.TechnologyTileBackground, ChemPalette.TechnologyTileForeground)
    val music = DemoCourse("El lenguaje de la música", "Música", "Curso abierto", .15f, Icons.Outlined.MusicNote, ChemPalette.MusicTileBackground, ChemPalette.MusicTileForeground)
    val strategy = DemoCourse("Piensa como ajedrecista", "Estrategia", "Curso abierto", 0f, Icons.Outlined.EmojiEvents, ChemPalette.StrategyTileBackground, ChemPalette.StrategyTileForeground)
    val maths = DemoCourse("Refuerzo de matemática", "Matemática · Privado", "Aula privada · Grupo de 3", 0f, Icons.Outlined.Functions, ChemPalette.ScienceTileBackground, ChemPalette.ScienceTileForeground, isPrivate = true)
    val utpCourses = listOf(
        DemoCourse("Redes y Comunicación de Datos II", "Ingeniería de Sistemas", "4 créditos · Prof. José García · DEMO", .28f, Icons.Outlined.Code, ChemPalette.TechnologyTileBackground, ChemPalette.TechnologyTileForeground),
        DemoCourse("Mecánica Clásica", "Ciencias básicas", "4 créditos · Aula 204 · DEMO", .40f, Icons.Outlined.Science, ChemPalette.ScienceTileBackground, ChemPalette.ScienceTileForeground),
        DemoCourse("Formación para la Investigación", "Investigación", "3 créditos · DEMO", .50f, Icons.Outlined.Functions, ChemPalette.ScienceTileBackground, ChemPalette.ScienceTileForeground)
    )
    val senatiCourses = listOf(
        DemoCourse("Programación de aplicaciones", "Software", "4 créditos · DEMO", .35f, Icons.Outlined.Code, ChemPalette.TechnologyTileBackground, ChemPalette.TechnologyTileForeground),
        DemoCourse("Fundamentos de redes", "Tecnología", "3 créditos · DEMO", .20f, Icons.Outlined.Functions, ChemPalette.ScienceTileBackground, ChemPalette.ScienceTileForeground)
    )
    val explore = listOf(chemistry, programming, music, strategy)
    val myCourses = listOf(chemistry, music, maths)
}
