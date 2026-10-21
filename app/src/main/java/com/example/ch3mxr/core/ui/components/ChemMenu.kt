package com.example.ch3mxr.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.theme.ChemDimens
import com.example.ch3mxr.core.ui.theme.ChemShapes

/** Grupo reutilizable para Perfil, Configuración y resultados de búsqueda. */
@Composable
fun ChemMenuGroup(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val colors = MaterialTheme.colorScheme
    Surface(modifier = modifier.fillMaxWidth(), shape = ChemShapes.Component,
        color = colors.surface,
        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.3f))) {
        Column(content = content)
    }
}

@Composable
fun ChemMenuRow(
    icon: ImageVector,
    title: String,
    subtitle: String = "",
    onClick: () -> Unit,
    showDivider: Boolean = true,
    trailingText: String? = null
) {
    val colors = MaterialTheme.colorScheme
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
                .defaultMinSize(minHeight = ChemDimens.MenuRowMinHeight)
                .padding(horizontal = 15.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(icon, contentDescription = null, tint = colors.primary,
                modifier = Modifier.size(22.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurface, fontWeight = FontWeight.SemiBold)
                if (subtitle.isNotBlank()) Text(subtitle,
                    style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant)
            }
            if (trailingText != null) Text(trailingText, color = colors.primary,
                style = MaterialTheme.typography.labelSmall)
            Icon(Icons.Outlined.ChevronRight, contentDescription = null,
                tint = colors.onSurfaceVariant, modifier = Modifier.size(20.dp))
        }
        if (showDivider) HorizontalDivider(color = colors.outline.copy(alpha = .22f))
    }
}
