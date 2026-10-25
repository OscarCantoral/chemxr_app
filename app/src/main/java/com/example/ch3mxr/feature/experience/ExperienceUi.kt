package com.example.ch3mxr.feature.experience

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.components.ChemPanel
import com.example.ch3mxr.core.ui.theme.ChemShapes
import com.example.ch3mxr.feature.main.components.*
import com.example.ch3mxr.feature.profile.model.StudySpace

@Composable
fun MockNote() {
    // Aviso retirado de la interfaz: las operaciones siguen siendo locales hasta enlazar la API.
}

@Composable
fun DemoNavRow(title: String, subtitle: String = "", icon: ImageVector = Icons.Outlined.ChevronRight, onClick: () -> Unit) {
    Surface(
        onClick = onClick, modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface, shape = ChemShapes.Component,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha=.27f))
    ) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, null, tint=MaterialTheme.colorScheme.primary, modifier=Modifier.size(23.dp))
            Column(Modifier.weight(1f), verticalArrangement=Arrangement.spacedBy(3.dp)) {
                Text(title, fontWeight=FontWeight.Bold, style=MaterialTheme.typography.bodyMedium)
                if (subtitle.isNotEmpty()) Text(subtitle, color=MaterialTheme.colorScheme.onSurfaceVariant, style=MaterialTheme.typography.labelSmall)
            }
            Icon(Icons.Outlined.ChevronRight, "Abrir", tint=MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun DemoBadge(text: String, color: Color = MaterialTheme.colorScheme.primary) {
    Surface(color = color.copy(alpha = .15f), shape=ChemShapes.Component) {
        Text(text, Modifier.padding(horizontal=9.dp, vertical=5.dp), style=MaterialTheme.typography.labelSmall,
            color=color, fontWeight=FontWeight.SemiBold)
    }
}

@Composable
fun SpaceContextHeader(space: StudySpace, subtitle: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.spacedBy(9.dp)) {
        SpaceMark(space)
        Column(Modifier.weight(1f)) {
            Eyebrow(space.name)
            Text(subtitle, color=MaterialTheme.colorScheme.onSurfaceVariant, style=MaterialTheme.typography.labelSmall)
        }
        if (space.academic) DemoBadge("Institucional")
    }
}

@Composable
fun GroupPanel(title: String, subtitle: String, content: @Composable ColumnScope.()->Unit) {
    ChemPanel {
        Text(title, fontWeight=FontWeight.Bold, style=MaterialTheme.typography.titleMedium)
        if (subtitle.isNotBlank()) {
            Spacer(Modifier.height(5.dp))
            Text(subtitle, style=MaterialTheme.typography.bodySmall, color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
fun SimpleInfo(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical=5.dp), horizontalArrangement=Arrangement.spacedBy(10.dp)) {
        Text(label, Modifier.weight(1f), style=MaterialTheme.typography.bodySmall, color=MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight=FontWeight.SemiBold, style=MaterialTheme.typography.bodySmall)
    }
}
