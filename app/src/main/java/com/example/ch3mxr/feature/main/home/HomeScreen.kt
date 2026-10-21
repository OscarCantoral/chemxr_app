package com.example.ch3mxr.feature.main.home

import com.example.ch3mxr.core.ui.theme.ChemPalette

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import com.example.ch3mxr.core.ui.theme.ChemShapes
import com.example.ch3mxr.core.ui.components.ChemButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ch3mxr.feature.main.components.*
import com.example.ch3mxr.feature.main.model.DemoCatalog

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var category by rememberSaveable { mutableStateOf("Todo") }
    val accent = MaterialTheme.colorScheme.primary
    MainPage(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(Modifier.size(6.dp).background(accent, ChemShapes.Component))
            Eyebrow("Mi espacio")
        }
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Heading("Hola, Oscar.", 31)
            Text("¿Qué vas a descubrir hoy?", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        FeaturedCourseCard()
        DemoPanel {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconTile(Icons.Outlined.AutoAwesome, ChemPalette.TutorTileBackground, accent, 46)
                Column(Modifier.weight(1f)) {
                    Text("Una buena pregunta lo cambia todo.", fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(6.dp))
                    Text("Repasa con tu Tutor IA", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Icon(Icons.Outlined.ArrowForward, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(2.dp))
        SectionHeading("Explora algo nuevo", "Ver todos")
        Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Todo", "Ciencia", "Tecnología").forEach { item ->
                    DemoFilterButton(item, category == item, Modifier.weight(1f)) { category = item }
                }
            }
            DemoFilterButton("Música", category == "Música", Modifier.fillMaxWidth()) { category = "Música" }
        }
        val courses = DemoCatalog.explore.filter {
            category == "Todo" || it.area == category
        }
        courses.forEach { course -> CourseCard(course) }
        DemoPanel {
            Eyebrow("PUBLICIDAD · EJEMPLO")
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Outlined.Public, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Column(Modifier.weight(1f)) {
                    Text("Una idea puede cambiar tu mundo.", style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Nexo Ciencia · Aprende haciendo", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("Ver", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = accent)
            }
            Spacer(Modifier.height(13.dp))
            Text("Ocultar anuncios hoy", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        SectionHeading("Descubre tu área", "Ver más")
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconTile(Icons.Outlined.Public, ChemPalette.OceanTileBackground, ChemPalette.OceanTileForeground, 78)
            Column(Modifier.weight(1f)) {
                Eyebrow("Ingeniería · 4 min")
                Spacer(Modifier.height(5.dp))
                Text("Robótica para explorar el océano", color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold, fontSize = 15.sp, lineHeight = 19.sp)
                Text("Lectura de demostración", color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall)
            }
            Icon(Icons.Outlined.ArrowForward, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        SectionHeading("En tu agenda")
        DemoPanel {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Outlined.Science, null, tint = accent)
                Column(Modifier.weight(1f)) {
                    Text("Repasar estructura atómica", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                    Text("2026-10-19 · 18:00", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Icon(Icons.Outlined.ArrowForward, null, tint = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun FeaturedCourseCard() {
    val lime = MaterialTheme.colorScheme.primary
    Surface(
        modifier = Modifier.fillMaxWidth().height(248.dp),
        color = ChemPalette.FeaturedCourseBackground, shape = ChemShapes.Component,
        border = BorderStroke(1.dp, lime.copy(alpha = .18f))
    ) {
        Box(Modifier.fillMaxSize()) {
            Icon(
                Icons.Outlined.Science, null,
                modifier = Modifier.align(Alignment.CenterEnd).offset(x = 28.dp, y = 13.dp).size(175.dp),
                tint = lime.copy(alpha = .28f)
            )
            Column(
                modifier = Modifier.fillMaxHeight().padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Surface(color = lime.copy(alpha = .10f), shape = ChemShapes.Component) {
                    Text("CONTINÚA APRENDIENDO", modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                        fontSize = 9.sp, color = lime, fontWeight = FontWeight.Bold)
                }
                Text("Química\nbásica", color = Color.White, fontSize = 27.sp,
                    fontWeight = FontWeight.Bold, lineHeight = 28.sp)
                Text("Conceptos fundamentales", color = ChemPalette.FeaturedText, style = MaterialTheme.typography.labelMedium)
                ProgressStrip(.40f, modifier = Modifier.width(140.dp))
                Text("40 % completado", color = ChemPalette.FeaturedTextMuted, style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.weight(1f))
                ChemButton(
                    text = "Continuar",
                    onClick = { /* Demo: al implementar el curso, conectar aquí */ },
                    modifier = Modifier.width(160.dp),
                    leadingIcon = Icons.Outlined.ArrowForward
                )
            }
        }
    }
}
