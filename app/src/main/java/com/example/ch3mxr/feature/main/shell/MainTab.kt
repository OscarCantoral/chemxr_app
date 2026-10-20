package com.example.ch3mxr.feature.main.shell

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainTab(
    val title: String,
    val icon: ImageVector
) {
    HOME("Inicio", Icons.Outlined.Home),
    COURSES("Mis cursos", Icons.Outlined.MenuBook),
    COMMUNITY("Comunidad", Icons.Outlined.Groups),
    AGENDA("Agenda", Icons.Outlined.DateRange)
}