package com.example.ch3mxr.feature.main.courses

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.outlined.*
import com.example.ch3mxr.feature.profile.model.*
import com.example.ch3mxr.feature.experience.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import com.example.ch3mxr.core.ui.theme.ChemShapes
import com.example.ch3mxr.core.ui.components.ChemSelectablePanel
import com.example.ch3mxr.core.ui.components.ChemPanel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ch3mxr.feature.main.components.*
import com.example.ch3mxr.feature.main.model.DemoCatalog

@Composable
fun CoursesScreen(
    modifier: Modifier = Modifier,
    space: StudySpace = DemoStudySpaces.available.first(),
    onOpen:(String)->Unit = {}
) {
    var section by rememberSaveable(space.id) { mutableStateOf(if(space.academic) "Mis asignaturas" else "Mis aulas y cursos") }
    var status by rememberSaveable { mutableStateOf("Todos") }
    val initial = if (space.academic) {
        if(space.id == "utp") DemoCatalog.utpCourses else DemoCatalog.senatiCourses
    } else if (section == "Catálogo abierto") DemoCatalog.explore else DemoCatalog.myCourses
    val filtered = when (status) {
        "En curso" -> initial.filter { it.progress > 0f && it.progress < 1f }
        "Completados" -> initial.filter { it.progress >= 1f }
        else -> initial
    }
    MainLazyPage(modifier) {
        item {
        Eyebrow("${space.name} · Tu aprendizaje")
        }
        item {
        Column {
            Heading("Un paso más", 28)
            Text("hacia tu objetivo.", fontSize = 28.sp, lineHeight = 31.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
        }
        }
        item {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ShortcutTile("Mi progreso", Icons.Outlined.BarChart, Modifier.weight(1f).clickable { onOpen("record") })
            ShortcutTile("Mi estante", Icons.Outlined.BookmarkBorder, Modifier.weight(1f).clickable { onOpen("shelf") })
            ShortcutTile("Practicar", Icons.Outlined.Bolt, Modifier.weight(1f).clickable { onOpen("quiz") })
        }
        }
        item {
        Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, ChemShapes.Component)
            .padding(5.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            (if(space.academic) listOf("Mis asignaturas", "Periodo actual") else listOf("Mis aulas y cursos", "Catálogo abierto")).forEach { item ->
                ChemSelectablePanel(
                    selected = section == item,
                    modifier = Modifier.weight(1f),
                    onClick = { section = item }
                ) {
                    Box(Modifier.height(38.dp), contentAlignment = Alignment.Center) {
                        Text(item, style = MaterialTheme.typography.labelSmall,
                            color = if (section == item) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        }
        item {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("Todos", "En curso", "Completados").forEach { item ->
                DemoFilterButton(item, status == item, Modifier.weight(1f)) { status = item }
            }
        }
        }
        if(space.academic) {
            item { DemoNavRow("Matrícula institucional", "Seleccionar asignaturas y revisar créditos",Icons.Outlined.AppRegistration) {onOpen("enrollment")} }
            item { DemoNavRow("Calificaciones y asistencias", "Consulta resultados del periodo",Icons.Outlined.Assessment) {onOpen("record")} }
            item { DemoNavRow("Sesiones virtuales", "En vivo y grabaciones",Icons.Outlined.VideoCall) {onOpen("sessions")} }
        }
        if(!space.academic) item {
            DemoNavRow("Patrocinado · Taller de ciencias", "Publicidad de ejemplo · Abrir video",Icons.Outlined.SmartDisplay) {onOpen("adVideo")}
        }
        if (filtered.isEmpty()) {
            item {
                DemoPanel {
                    Text("No hay cursos en esta categoría (demostración).",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            items(filtered, key = { it.title }) { course -> CourseCard(course,Modifier.clickable {onOpen("course")}) }
        }
    }
}

@Composable
private fun ShortcutTile(text: String, icon: ImageVector, modifier: Modifier = Modifier) {
    val c = MaterialTheme.colorScheme
    Surface(
        modifier = modifier.height(88.dp), color = c.surface,
        border = BorderStroke(1.dp, c.outline.copy(alpha = .25f)),
        shape = ChemShapes.Component
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(6.dp)) {
            Icon(icon, null, tint = c.primary, modifier = Modifier.size(25.dp))
            Spacer(Modifier.height(7.dp))
            Text(text, fontSize = 10.sp, color = c.onSurface, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}
