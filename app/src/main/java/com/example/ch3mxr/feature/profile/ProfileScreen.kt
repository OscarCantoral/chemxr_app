package com.example.ch3mxr.feature.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ch3mxr.core.ui.components.*
import com.example.ch3mxr.core.ui.theme.ChemDimens
import com.example.ch3mxr.feature.main.components.Eyebrow
import com.example.ch3mxr.feature.main.components.MainPage
import com.example.ch3mxr.feature.profile.model.StudySpace

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    activeSpace: StudySpace,
    onSettings: () -> Unit,
    onLogout: () -> Unit,
    onMockAction: (String) -> Unit
) {
    val c = MaterialTheme.colorScheme
    MainPage(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp)) {
            ChemProfileAvatar(onClick = { onMockAction("Cambiar fotografía") },
                modifier = Modifier.size(ChemDimens.ProfileAvatarSize),
                avatarSize = ChemDimens.ProfileAvatarSize)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Oscar Cantoral", color = c.onBackground,
                    style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Text("@oscarcantoral", style = MaterialTheme.typography.bodySmall,
                    color = c.onSurfaceVariant)
            }
        }
        Text("Ciencia, tecnología y aprendizaje", color = c.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            ChemButton("Editar perfil", { onMockAction("Editar perfil") },
                modifier = Modifier.weight(1f))
            ChemButton("Ver perfil público", { onMockAction("Ver perfil público") },
                modifier = Modifier.weight(1f), style = ChemButtonStyle.OUTLINED)
        }
        Eyebrow("Mi aprendizaje · ${activeSpace.name}")
        ChemMenuGroup {
            ChemMenuRow(Icons.Outlined.EmojiEvents, "Logros",
                "Tu camino deja huella", { onMockAction("Logros") })
            ChemMenuRow(Icons.Outlined.Forum, "Mis consultas",
                "Conversaciones con tus docentes", { onMockAction("Mis consultas") }, showDivider = false)
        }
        ChemPanel(color = c.primary.copy(alpha = 0.09f)) {
            Eyebrow("Plan de mi espacio")
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Gratuito", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = c.onSurface)
                    Spacer(Modifier.height(5.dp))
                    Text("Aprender es solo el comienzo.",
                        style = MaterialTheme.typography.labelSmall, color = c.onSurfaceVariant)
                }
                ChemButton("Ver detalles", { onMockAction("Plan gratuito") },
                    height = ChemDimens.FilterHeight, style = ChemButtonStyle.MUTED)
            }
        }
        ChemMenuGroup {
            ChemMenuRow(Icons.Outlined.School, "¿Quieres compartir lo que sabes?",
                "Quiero enseñar", { onMockAction("Quiero enseñar") }, showDivider = false)
        }
        ChemMenuGroup {
            ChemMenuRow(Icons.Outlined.HelpOutline, "Ayuda y soporte",
                "Estamos para orientarte", { onMockAction("Ayuda y soporte") })
            ChemMenuRow(Icons.Outlined.Tune, "Escenarios del prototipo",
                "Probar UTP, cuenta gratuita y Pro", { onMockAction("Escenarios del prototipo") }, showDivider = false)
        }
        // Acceso alternativo además del engranaje superior.
        ChemButton("Configuración", onSettings, modifier = Modifier.fillMaxWidth(),
            style = ChemButtonStyle.OUTLINED, leadingIcon = Icons.Outlined.Settings)
        TextButton(onClick = onLogout) {
            Icon(Icons.Outlined.Logout, contentDescription = null, tint = c.error)
            Spacer(Modifier.width(10.dp))
            Text("Cerrar sesión", color = c.error)
        }
    }
}
