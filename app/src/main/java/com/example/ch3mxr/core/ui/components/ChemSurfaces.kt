package com.example.ch3mxr.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.theme.ChemDimens
import com.example.ch3mxr.core.ui.theme.ChemShapes

/** Una sola implementación de panel, utilizada desde todas las features. */
@Composable
fun ChemPanel(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = color,
        shape = ChemShapes.Component,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = .28f))
    ) {
        Column(modifier = Modifier.padding(ChemDimens.PanelPadding), content = content)
    }
}

/** Selección con bordes rectos; útil para chips, pestañas y días del calendario. */
@Composable
fun ChemSelectablePanel(
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = if (selected) colors.primary else colors.surface,
        contentColor = if (selected) colors.onPrimary else colors.onSurfaceVariant,
        shape = ChemShapes.Component,
        border = if (selected) null else BorderStroke(1.dp, colors.outline.copy(alpha = .30f))
    ) { content() }
}

@Composable
fun ChemIconTile(icon: ImageVector, background: Color, foreground: Color, size: Int = 76) {
    Box(
        modifier = Modifier.size(size.dp).background(background, ChemShapes.Component),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = foreground, modifier = Modifier.size((size * .52f).dp))
    }
}

@Composable
fun ChemProgress(progress: Float, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Box(modifier = modifier.height(4.dp).background(colors.outline.copy(alpha = .32f), ChemShapes.Component)) {
        Box(Modifier.fillMaxWidth(progress.coerceIn(0f, 1f))
            .fillMaxHeight().background(colors.primary, ChemShapes.Component))
    }
}

@Composable
fun ChemTextAction(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(onClick = onClick, modifier = modifier, shape = ChemShapes.Component) {
        Text(text, color = MaterialTheme.colorScheme.primary)
    }
}
