package com.example.ch3mxr.feature.learning

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.ViewInAr
import androidx.compose.material.icons.outlined.Code
import com.example.ch3mxr.core.ui.theme.ChemPalette
import com.example.ch3mxr.feature.main.model.DemoCatalog
import com.example.ch3mxr.feature.main.model.DemoCourse
import com.example.ch3mxr.feature.selection.CourseSelectionCatalog

object LearningCatalog {
    val free = listOf(
        DemoCatalog.chemistry.copy(id="adrom_chemistry", owner=CourseOwner.ADROM, instructor="Equipo ADROM", lessons=24),
        DemoCatalog.programming.copy(id="adrom_algorithm", owner=CourseOwner.ADROM, instructor="Equipo ADROM", lessons=18),
        DemoCatalog.music.copy(id="adrom_music", owner=CourseOwner.ADROM, instructor="Equipo ADROM", lessons=22),
        DemoCatalog.strategy.copy(id="adrom_strategy", owner=CourseOwner.ADROM, instructor="Equipo ADROM", lessons=16)
    )
    val pro = listOf(
        DemoCourse("Laboratorio de química con XR", "Ciencia", "Creado por ADROM", 0f,
            Icons.Outlined.Science, ChemPalette.ScienceTileBackground, ChemPalette.ScienceTileForeground,
            id="adrom_xr_pro", owner=CourseOwner.ADROM, access=CourseAccess.PRO, instructor="Equipo ADROM",
            level="Avanzado", duration="8 h", lessons=42, rating=4.9f, ratingsCount=86),
        DemoCourse("Modelado molecular avanzado", "Ciencia", "Creado por ADROM", 0f,
            Icons.Outlined.ViewInAr, ChemPalette.TechnologyTileBackground, ChemPalette.TechnologyTileForeground,
            id="adrom_molecules_pro", owner=CourseOwner.ADROM, access=CourseAccess.PRO, instructor="Equipo ADROM",
            level="Avanzado", duration="7 h", lessons=36, rating=4.8f, ratingsCount=94),
        DemoCourse("IA aplicada a proyectos STEM", "Tecnología", "Creado por ADROM", 0f,
            Icons.Outlined.Code, ChemPalette.TechnologyTileBackground, ChemPalette.TechnologyTileForeground,
            id="adrom_stem_pro", owner=CourseOwner.ADROM, access=CourseAccess.PRO, instructor="Equipo ADROM",
            level="Avanzado", duration="9 h", lessons=48, rating=4.9f, ratingsCount=112)
    )
    val paid = CourseSelectionCatalog.courses.map { product ->
        product.course.copy(id=product.id, owner=CourseOwner.TEACHER, access=CourseAccess.PURCHASE,
            instructor=product.instructor, priceCents=product.priceCents, duration=product.duration, lessons=product.lessons)
    }
    // Alterna modalidades para que las dos columnas permitan compararlas desde el inicio.
    val all = listOf(free[0], paid[0], pro[0], paid[1], free[1], pro[1], paid[2], paid[3], free[2], pro[2], paid[4], paid[5], free[3])
    val initialEnrollmentIds = listOf("adrom_chemistry", "adrom_music")
    fun find(id: String) = all.firstOrNull { it.id == id }
}
