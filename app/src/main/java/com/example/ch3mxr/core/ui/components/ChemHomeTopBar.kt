package com.example.ch3mxr.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    val c = MaterialTheme.colorScheme
    Column(Modifier.fillMaxWidth().background(c.background).statusBarsPadding()) {
        Row(
            modifier = Modifier.fillMaxWidth().height(ChemDimens.TopBarHeight)
                .padding(horizontal = ChemDimens.TopBarHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Surface(
                modifier = Modifier.size(ChemDimens.TopBarAvatarSize).clickable(onClick = onProfileClick),
                color = c.surface, shape = ChemShapes.Component,
                border = BorderStroke(1.dp, c.outline.copy(alpha = .35f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Person, "Perfil", tint = c.primary,
                        modifier = Modifier.size(23.dp))
                }
            }
            Surface(
                modifier = Modifier.weight(1f).height(ChemDimens.TopBarSearchHeight)
                    .clickable(onClick = onSearchClick),
                shape = ChemShapes.Component, color = c.surface,
                border = BorderStroke(1.dp, c.outline.copy(alpha = .32f))
            ) {
                Row(modifier = Modifier.padding(horizontal = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Outlined.Search, null, modifier = Modifier.size(17.dp),
                        tint = c.onSurfaceVariant)
                    Text(searchPlaceholder, style = MaterialTheme.typography.labelSmall,
                        color = c.onSurfaceVariant, maxLines = 1,
                        overflow = TextOverflow.Ellipsis)
                }
            }
            ChemButton(
                text = "IA", onClick = onAiClick,
                modifier = Modifier.width(34.dp),
                style = ChemButtonStyle.OUTLINED,
                height = ChemDimens.TopBarActionSize
            )
            Box(modifier = Modifier.size(ChemDimens.TopBarActionSize)
                .clickable(onClick = onNotificationsClick), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Notifications, "Notificaciones", tint = c.onBackground,
                    modifier = Modifier.size(23.dp))
                Box(Modifier.align(Alignment.TopEnd).size(5.dp).background(c.primary, ChemShapes.Component))
            }
        }
        HorizontalDivider(color = c.outline.copy(alpha = .30f), thickness = 1.dp)
    }
}
