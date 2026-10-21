package com.example.ch3mxr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.theme.ChemDimens
import com.example.ch3mxr.core.ui.theme.ChemShapes

enum class ChemButtonStyle { FILLED, OUTLINED, MUTED }

/** Botón visual compartido; sin ripple ni transición de colores. */
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
    val background = when (style) {
        ChemButtonStyle.FILLED -> colors.primary
        ChemButtonStyle.OUTLINED -> Color.Transparent
        ChemButtonStyle.MUTED -> colors.surface
    }
    val foreground = when (style) {
        ChemButtonStyle.FILLED -> colors.onPrimary
        ChemButtonStyle.OUTLINED -> colors.onSurfaceVariant
        ChemButtonStyle.MUTED -> colors.onSurface
    }
    val interaction = remember { MutableInteractionSource() }
    val borderModifier = if (style == ChemButtonStyle.OUTLINED) {
        Modifier.border(1.dp, colors.outline.copy(alpha = 0.45f), ChemShapes.Component)
    } else Modifier

    Box(
        modifier = modifier
            .height(height)
            .background(if (enabled) background else background.copy(alpha = 0.45f), ChemShapes.Component)
            .then(borderModifier)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(leadingIcon, contentDescription = null,
                    modifier = Modifier.size(17.dp), tint = foreground)
                Spacer(Modifier.width(6.dp))
            }
            Text(text = text, color = foreground.copy(alpha = if (enabled) 1f else .45f),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
    }
}
