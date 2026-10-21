package com.example.ch3mxr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.theme.ChemDimens

/** Cabecera compartida de Perfil, Configuración y otras pantallas detalladas. SIN Tutor IA. */
@Composable
fun ChemDetailTopBar(
    title: String,
    onBack: () -> Unit,
    actionIcon: ImageVector? = null,
    actionDescription: String = "Acción",
    onAction: () -> Unit = {},
    onTitleClick: (() -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme
    Column(Modifier.fillMaxWidth().background(colors.background).statusBarsPadding()) {
        Row(
            modifier = Modifier.fillMaxWidth().height(ChemDimens.DetailTopBarHeight)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChemIconAction(Icons.Outlined.ArrowBack, "Volver", onBack, tint = colors.onBackground)
            if (onTitleClick != null) {
                Row(
                    modifier = Modifier.weight(1f).clickable(onClick = onTitleClick)
                        .padding(start = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(title, modifier = Modifier.weight(1f, fill = false),
                        color = colors.onBackground, fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Icon(Icons.Outlined.KeyboardArrowDown,
                        contentDescription = "Cambiar espacio", modifier = Modifier.size(20.dp),
                        tint = colors.onBackground)
                }
            } else {
                Text(title, modifier = Modifier.weight(1f).padding(start = 7.dp),
                    color = colors.onBackground, fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            if (actionIcon != null) {
                ChemIconAction(actionIcon, actionDescription, onAction, tint = colors.onBackground)
            }
        }
        HorizontalDivider(color = colors.outline.copy(alpha = 0.30f))
    }
}
