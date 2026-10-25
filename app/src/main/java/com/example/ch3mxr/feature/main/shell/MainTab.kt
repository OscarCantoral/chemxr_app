package com.example.ch3mxr.feature.main.shell

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainTab(val title: String, val searchHint: String, val icon: ImageVector) {
    HOME("Inicio", "¿Qué quieres aprender?", Icons.Outlined.Home),
    COURSES("Mis cursos", "Buscar en mis cursos", Icons.Outlined.MenuBook),
    CAMPUS("Campus", "Buscar en tu institución", Icons.Outlined.Home),
    COMMUNITY("Comunidad", "Buscar comunidades", Icons.Outlined.Groups),
    AGENDA("Agenda", "Buscar actividades", Icons.Outlined.DateRange)
}
