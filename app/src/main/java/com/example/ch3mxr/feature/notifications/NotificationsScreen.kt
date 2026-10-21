package com.example.ch3mxr.feature.notifications

import com.example.ch3mxr.core.ui.theme.ChemPalette

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import com.example.ch3mxr.core.ui.theme.ChemDimens
import com.example.ch3mxr.core.ui.theme.ChemShapes
import com.example.ch3mxr.core.ui.components.ChemTextAction
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Science
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
fun NotificationsTopBar(onBackClick: () -> Unit) {
    val c = MaterialTheme.colorScheme
    Column(Modifier.fillMaxWidth().background(c.background).statusBarsPadding()) {
        Row(
            modifier = Modifier.fillMaxWidth().height(ChemDimens.TopBarHeight).padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Outlined.ArrowBack, "Volver", tint = c.onBackground)
            }
            Text("Notificaciones", fontSize = 23.sp, fontWeight = FontWeight.Bold, color = c.onBackground)
        }
        HorizontalDivider(color = c.outline.copy(alpha = .30f))
    }
}

@Composable
fun NotificationsScreen(modifier: Modifier = Modifier) {
    var filter by rememberSaveable { mutableStateOf("Todas") }
    var allRead by rememberSaveable { mutableStateOf(false) }
    // No existe repositorio: el estado es solo visual y se reinicia con el prototipo.
    MainPage(modifier.navigationBarsPadding()) {
        Eyebrow("Mi espacio")
        Spacer(Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Heading("Al día.", 31)
            ChemTextAction(text = "Marcar leídas", onClick = { allRead = true })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DemoFilterButton("Todas", filter == "Todas", Modifier.weight(1f)) { filter = "Todas" }
            DemoFilterButton("Sin leer", filter == "Sin leer", Modifier.weight(1f)) { filter = "Sin leer" }
        }
        if (filter == "Todas" || !allRead) {
            NotificationPreview(
                title = "Tu próximo descubrimiento",
                message = "Continúa la lección de estructura atómica.",
                isNew = !allRead,
                isScience = true
            )
            NotificationPreview(
                title = "Una nueva conversación",
                message = "Tu comunidad tiene una pregunta para ti.",
                isNew = !allRead,
                isScience = false
            )
        } else {
            DemoPanel {
                Text("No tienes notificaciones sin leer.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun NotificationPreview(title: String, message: String, isNew: Boolean, isScience: Boolean) {
    val c = MaterialTheme.colorScheme
    Surface(modifier = Modifier.fillMaxWidth(), color = c.surface,
        shape = ChemShapes.Component,
        border = BorderStroke(1.dp, c.outline.copy(alpha = .32f))) {
        Row {
            if (isNew) Box(Modifier.width(4.dp).height(140.dp).background(c.primary))
            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                IconTile(
                    icon = if (isScience) Icons.Outlined.Science else Icons.Outlined.ChatBubbleOutline,
                    background = ChemPalette.NotificationTileBackground, foreground = c.primary, size = 52
                )
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text(title, fontWeight = FontWeight.Bold, color = c.onSurface,
                        fontSize = 16.sp, lineHeight = 19.sp)
                    Text(message, color = c.onSurfaceVariant, fontSize = 14.sp, lineHeight = 21.sp)
                    if (isNew) Text("Nueva", color = c.primary, fontSize = 12.sp,
                        fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
