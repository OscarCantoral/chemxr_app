package com.example.ch3mxr.feature.main.agenda

import com.example.ch3mxr.core.ui.theme.ChemPalette

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import com.example.ch3mxr.core.ui.theme.ChemShapes
import com.example.ch3mxr.core.ui.components.ChemSelectablePanel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.TrendingUp
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
fun AgendaScreen(modifier: Modifier = Modifier) {
    var day by rememberSaveable { mutableStateOf(19) }
    var category by rememberSaveable { mutableStateOf("Todo") }
    MainPage(modifier) {
        Eyebrow("Mi espacio · A tu ritmo")
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Heading("Tu agenda.", 28)
            Icon(Icons.Outlined.CalendarToday, null, tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(25.dp))
        }
        Text("Octubre de 2026", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            listOf("Lu", "Ma", "Mi", "Ju", "Vi", "Sá", "Do").forEachIndexed { index, name ->
                val selected = (19+index) == day
                val c = MaterialTheme.colorScheme
                ChemSelectablePanel(
                    selected = selected,
                    modifier = Modifier.weight(1f).height(80.dp),
                    onClick = { day = 19 + index }
                ) {
                    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(name, fontSize = 10.sp, color = if(selected) c.onPrimary else c.onSurfaceVariant)
                        Spacer(Modifier.height(7.dp))
                        Text("${19+index}", fontSize = 17.sp, fontWeight = FontWeight.Bold,
                            color = if(selected) c.onPrimary else c.onSurfaceVariant)
                        Text("•", color = if(selected) c.onPrimary else c.onSurfaceVariant, fontSize = 11.sp)
                    }
                }
            }
        }
        Text("Ir a otra fecha", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        DemoPanel {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${day.toString().padStart(2, '0')}/10/2026", modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Icon(Icons.Outlined.CalendarToday, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(18.dp))
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Todo", "Clase", "Entrega").forEach { item ->
                    DemoFilterButton(item, category == item, Modifier.weight(1f)) { category = item }
                }
            }
            DemoFilterButton("Plan personal", category == "Plan personal", Modifier.fillMaxWidth()) { category = "Plan personal" }
        }
        Spacer(Modifier.height(9.dp))
        SectionHeading("Tu día, paso a paso", "Planificar estudio")
        if (day == 19 && (category == "Todo" || category == "Plan personal")) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("18:00", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp,
                    modifier = Modifier.padding(top = 8.dp))
                Column(modifier = Modifier.weight(1f).padding(start = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(color = ChemPalette.AgendaChipBackground, shape = ChemShapes.Component) {
                        Text("Plan personal", modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                            fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    Text("Repasar estructura atómica", color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Química básica", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    Text("Marcar realizada", color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Text("No hay actividades de ejemplo para este filtro.",
                color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .25f))
        DemoPanel {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconTile(Icons.Outlined.TrendingUp, ChemPalette.NotificationTileBackground, MaterialTheme.colorScheme.primary, 45)
                Column {
                    Eyebrow("Mi objetivo semanal")
                    Spacer(Modifier.height(7.dp))
                    Text("0 de 4 actividades", fontSize = 21.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface)
                    Text("Haz del aprendizaje un hábito.", fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text("Ajustar objetivo", fontSize = 12.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
