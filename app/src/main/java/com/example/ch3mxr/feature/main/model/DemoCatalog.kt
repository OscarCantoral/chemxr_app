package com.example.ch3mxr.feature.main.model

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
    val chemistry = DemoCourse("Química básica", "Ciencia", "Curso abierto", .40f, Icons.Outlined.Science, Color(0xFF263F33), Color(0xFFCDF1A9))
    val programming = DemoCourse("Tu primer algoritmo", "Tecnología", "Curso abierto", 0f, Icons.Outlined.Code, Color(0xFF293954), Color(0xFFB2D9FF))
    val music = DemoCourse("El lenguaje de la música", "Música", "Curso abierto", .15f, Icons.Outlined.MusicNote, Color(0xFF45354A), Color(0xFFEDC7F4))
    val strategy = DemoCourse("Piensa como ajedrecista", "Estrategia", "Curso abierto", 0f, Icons.Outlined.EmojiEvents, Color(0xFF283E35), Color(0xFFCBF5A9))
    val maths = DemoCourse("Refuerzo de matemática", "Matemática · Privado", "Aula privada · Grupo de 3", 0f, Icons.Outlined.Functions, Color(0xFF263F33), Color(0xFFCDF1A9), isPrivate = true)
    val explore = listOf(chemistry, programming, music, strategy)
    val myCourses = listOf(chemistry, music, maths)
}
