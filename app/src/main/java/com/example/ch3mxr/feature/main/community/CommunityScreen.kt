package com.example.ch3mxr.feature.main.community

import com.example.ch3mxr.core.ui.theme.ChemPalette

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import com.example.ch3mxr.core.ui.theme.ChemShapes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Public
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

@Composable
fun CommunityScreen(modifier: Modifier = Modifier) {
    var groupFilter by rememberSaveable { mutableStateOf("Descubrir") }
    MainLazyPage(modifier) {
        item {
        Eyebrow("Mi espacio · Aprender juntos")
        }
        item {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Heading("Las ideas crecen", 26)
            Text("cuando se comparten.", fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary, fontSize = 25.sp, lineHeight = 29.sp)
        }
        }
        item {
        SectionHeading("Tus comunidades", "Crear comunidad")
        }
        item {
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            DemoFilterButton("Descubrir", groupFilter == "Descubrir", Modifier.weight(1f)) { groupFilter = "Descubrir" }
            DemoFilterButton("Mis comunidades", groupFilter == "Mis comunidades", Modifier.weight(1f)) { groupFilter = "Mis comunidades" }
        }
        }
        item {
        if (groupFilter == "Descubrir" || groupFilter == "Mis comunidades") {
            CommunityTile("Curiosos por la ciencia", "Preguntar es el primer paso", "342 miembros · Eres miembro", science = true)
        }
        }
        item {
        if (groupFilter == "Descubrir") {
            CommunityTile("Programamos juntos", "Ideas que se convierten en proyectos", "186 miembros", science = false)
        }
        }
        item {
        Spacer(Modifier.height(8.dp))
        }
        item {
        SectionHeading("Conversaciones que inspiran")
        }
        item {
        DemoPanel {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(35.dp).background(ChemPalette.MusicHighlight, ChemShapes.Component),
                    contentAlignment = Alignment.Center) {
                    Text("LT", fontSize = 11.sp, color = ChemPalette.MusicAlternateBackground)
                }
                Spacer(Modifier.width(11.dp))
                Column(Modifier.weight(1f)) {
                    Text("Lucía Torres", fontWeight = FontWeight.Bold, fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface)
                    Text("Curiosos por la ciencia", fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("Hace 2 h", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(18.dp))
            Text("¿Qué sensores usarían para un robot que detecta obstáculos? Estoy preparando mi primer proyecto.",
                color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, lineHeight = 21.sp)
            Spacer(Modifier.height(14.dp))
            Box(modifier = Modifier.fillMaxWidth().height(130.dp)
                .background(ChemPalette.CommunityTileBackground, ChemShapes.Component),
                contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.Code, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(44.dp))
                    Text("COMPARTE TU IDEA", color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        }
    }
}

@Composable
private fun CommunityTile(title: String, subtitle: String, footer: String, science: Boolean) {
    DemoPanel {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(13.dp)) {
            IconTile(
                icon = if (science) Icons.Outlined.Public else Icons.Outlined.Code,
                background = ChemPalette.ScienceAlternateBackground,
                foreground = MaterialTheme.colorScheme.primary,
                size = 60
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp, lineHeight = 17.sp)
                Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp)
                Text(footer, fontSize = 10.sp,
                    color = if (footer.contains("Eres miembro")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Outlined.ArrowForward, null, modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurface)
        }
    }
}
