package com.example.ch3mxr.feature.selection

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Functions
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.ViewInAr
import com.example.ch3mxr.core.model.*
import com.example.ch3mxr.core.ui.theme.ChemPalette
import com.example.ch3mxr.feature.main.model.DemoCourse

/** Productos y precios ficticios del prototipo. Los cursos abiertos siguen siendo gratuitos. */
data class SelectionCourse(
  val id: String,
  val course: DemoCourse,
  val instructor: String,
  val duration: String,
  val lessons: Int,
  val priceCents: Int,
  val regularPriceCents: Int,
)

object CourseSelectionCatalog {
  val courses =
    listOf(
      SelectionCourse(
        id = "chemistry_ar",
        course =
          DemoCourse(
            "Química aplicada con realidad aumentada",
            "Ciencia",
            "Curso completo · A tu ritmo",
            0f,
            Icons.Outlined.Science,
            ChemPalette.ScienceTileBackground,
            ChemPalette.ScienceTileForeground,
          ),
        instructor = "Andrea Ríos",
        duration = "6 h",
        lessons = 32,
        priceCents = 3990,
        regularPriceCents = 8990,
      ),
      SelectionCourse(
        id = "kotlin_basics",
        course =
          DemoCourse(
            "Kotlin desde cero",
            "Tecnología",
            "Curso completo · A tu ritmo",
            0f,
            Icons.Outlined.Code,
            ChemPalette.TechnologyTileBackground,
            ChemPalette.TechnologyTileForeground,
          ),
        instructor = "Diego Torres",
        duration = "8 h",
        lessons = 45,
        priceCents = 4990,
        regularPriceCents = 9990,
      ),
      SelectionCourse(
        id = "physics_motion",
        course =
          DemoCourse(
            "Física: movimiento y fuerzas",
            "Ciencia",
            "Curso completo · A tu ritmo",
            0f,
            Icons.Outlined.Functions,
            ChemPalette.ScienceTileBackground,
            ChemPalette.ScienceTileForeground,
          ),
        instructor = "Lucía Vega",
        duration = "5 h",
        lessons = 28,
        priceCents = 2990,
        regularPriceCents = 5990,
      ),
      SelectionCourse(
        id = "modeling_3d",
        course =
          DemoCourse(
            "Introducción al modelado 3D",
            "Tecnología",
            "Curso completo · A tu ritmo",
            0f,
            Icons.Outlined.ViewInAr,
            ChemPalette.TechnologyTileBackground,
            ChemPalette.TechnologyTileForeground,
          ),
        instructor = "Valeria Soto",
        duration = "7 h",
        lessons = 36,
        priceCents = 5990,
        regularPriceCents = 11990,
      ),
      SelectionCourse(
        id = "excel_data",
        course =
          DemoCourse(
            "Excel para analizar datos",
            "Tecnología",
            "Curso completo · A tu ritmo",
            0f,
            Icons.Outlined.BarChart,
            ChemPalette.TechnologyTileBackground,
            ChemPalette.TechnologyTileForeground,
          ),
        instructor = "Paola Núñez",
        duration = "6 h",
        lessons = 30,
        priceCents = 3490,
        regularPriceCents = 7990,
      ),
      SelectionCourse(
        id = "music_production",
        course =
          DemoCourse(
            "Crea tu primera canción",
            "Música",
            "Curso completo · A tu ritmo",
            0f,
            Icons.Outlined.MusicNote,
            ChemPalette.MusicTileBackground,
            ChemPalette.MusicTileForeground,
          ),
        instructor = "Javier Luna",
        duration = "4 h",
        lessons = 24,
        priceCents = 2490,
        regularPriceCents = 5490,
      ),
    )

  val initialSelectionIds = listOf("chemistry_ar", "kotlin_basics")

  fun find(id: String): SelectionCourse? = courses.firstOrNull { it.id == id }
}

/** Importes en céntimos para que sumar/quitar cursos no introduzca errores decimales. */
fun formatCoursePrice(cents: Int): String =
  "S/ ${cents / 100}.${(cents % 100).toString().padStart(2, '0')}"
