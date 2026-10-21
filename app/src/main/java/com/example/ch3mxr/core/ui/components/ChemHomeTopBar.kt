package com.example.ch3mxr.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.theme.ChemDimens
import com.example.ch3mxr.core.ui.theme.ChemShapes

@Composable
fun ChemHomeTopBar(
    searchPlaceholder: String,
    onProfileClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onAiClick: () -> Unit = {},
    onNotificationsClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.background)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(ChemDimens.TopBarHeight)
                .padding(horizontal = ChemDimens.TopBarHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            // Avatar circular: excepción intencional a ChemShapes.Component.
            ChemProfileAvatar(onClick = onProfileClick)

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(ChemDimens.TopBarSearchHeight)
                    .clickable(onClick = onSearchClick),
                shape = ChemShapes.Component,
                color = colors.surface,
                border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.32f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp),
                        tint = colors.onSurfaceVariant
                    )
                    Text(
                        text = searchPlaceholder,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Sólo PNG, sin fondo ni borde. Cambia según tema.
            ChemAiButton(onClick = onAiClick)

            Box(
                modifier = Modifier
                    .size(ChemDimens.TopBarActionSize)
                    .clickable(onClick = onNotificationsClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notificaciones",
                    tint = colors.onBackground,
                    modifier = Modifier.size(23.dp)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(5.dp)
                        .background(colors.primary, ChemShapes.Component)
                )
            }
        }

        HorizontalDivider(
            color = colors.outline.copy(alpha = 0.30f),
            thickness = 1.dp
        )
    }
}
