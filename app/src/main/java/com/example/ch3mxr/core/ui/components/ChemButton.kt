package com.example.ch3mxr.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.theme.ChemDimens
import com.example.ch3mxr.core.ui.theme.ChemShapes

enum class ChemButtonStyle { FILLED, OUTLINED, MUTED }

/** Botón común para Login, Curso destacado, filtros y futuras pantallas. */
@Composable
fun ChemButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: ChemButtonStyle = ChemButtonStyle.FILLED,
    height: Dp = ChemDimens.ButtonHeight,
    leadingIcon: ImageVector? = null
) {
    val colors = MaterialTheme.colorScheme
    val bg = when (style) {
        ChemButtonStyle.FILLED -> colors.primary
        ChemButtonStyle.OUTLINED -> Color.Transparent
        ChemButtonStyle.MUTED -> colors.surface
    }
    val fg = when (style) {
        ChemButtonStyle.FILLED -> colors.onPrimary
        ChemButtonStyle.OUTLINED -> colors.onSurfaceVariant
        ChemButtonStyle.MUTED -> colors.onSurface
    }
    Button(
        onClick = onClick,
        modifier = modifier.height(height),
        enabled = enabled,
        shape = ChemShapes.Component,
        border = if (style == ChemButtonStyle.OUTLINED) BorderStroke(1.dp, colors.outline.copy(alpha = .45f)) else null,
        colors = ButtonDefaults.buttonColors(containerColor = bg, contentColor = fg),
        contentPadding = PaddingValues(horizontal = 9.dp)
    ) {
        if (leadingIcon != null) {
            Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(17.dp))
            Spacer(Modifier.width(6.dp))
        }
        Text(text, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}
