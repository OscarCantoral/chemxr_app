package com.example.ch3mxr.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.components.*
import com.example.ch3mxr.core.ui.theme.ChemAccent
import com.example.ch3mxr.core.ui.theme.ChemDimens
import com.example.ch3mxr.core.ui.theme.ThemeMode
import com.example.ch3mxr.feature.main.components.Eyebrow
import com.example.ch3mxr.feature.main.components.Heading
import com.example.ch3mxr.feature.main.components.MainPage

/** Opciones de demostración: solo tema/color cambian la interfaz; el resto es estado local. */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    themeMode: ThemeMode,
    accent: ChemAccent,
    onThemeChange: (ThemeMode) -> Unit,
    onAccentChange: (ChemAccent) -> Unit,
    onMockAction: (String) -> Unit,
    academicSpace: Boolean = false,
    onOpen: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("chemxr_demo_preferences", android.content.Context.MODE_PRIVATE) }
    var notifications by remember { mutableStateOf(prefs.getBoolean("enable_notifications", true)) }
    var push by remember { mutableStateOf(prefs.getBoolean("push_academic", true)) }
    var email by remember { mutableStateOf(prefs.getBoolean("email_digest", false)) }
    var aiReminder by remember { mutableStateOf(prefs.getBoolean("ai_reminders", true)) }
    var adverts by remember { mutableStateOf(prefs.getBoolean("ad_notifications", false)) }

    MainPage(modifier) {
        Eyebrow("A tu manera")
        Heading("Tu experiencia,\ntus preferencias.", 27)
        Eyebrow("Cuenta")
        ChemMenuGroup {
            ChemMenuRow(Icons.Outlined.Person, "Cuenta y datos personales", onClick = { onOpen("personalData") })
            ChemMenuRow(Icons.Outlined.Lock, "Privacidad", onClick = { onOpen("privacy") })
            ChemMenuRow(Icons.Outlined.VpnKey, "Seguridad y cuentas vinculadas",
                onClick = { onOpen("security") }, showDivider = false)
        }
        Eyebrow("Experiencia")
        ChemPanel {
            Text("Apariencia", color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(9.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                listOf(ThemeMode.LIGHT to "Claro", ThemeMode.DARK to "Oscuro")
                    .forEach { (value, text) ->
                        ChemButton(text, { onThemeChange(value) }, Modifier.weight(1f),
                            style = if (themeMode == value) ChemButtonStyle.FILLED else ChemButtonStyle.OUTLINED,
                            height = ChemDimens.FilterHeight)
                    }
            }
            Spacer(Modifier.height(18.dp))
            Text("Color principal", color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(9.dp))
            if (academicSpace) {
                Text("El color y los iconos pertenecen a la identidad de la institución. Solo puedes cambiar el tema claro/oscuro.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    ChemAccent.entries.forEach { a ->
                        ChemButton(a.title, { onAccentChange(a) }, Modifier.weight(1f),
                            style = if (accent == a) ChemButtonStyle.FILLED else ChemButtonStyle.OUTLINED,
                            height = ChemDimens.FilterHeight)
                    }
                }
            }
            Spacer(Modifier.height(19.dp))
            ChemDemoToggleRow("Activar notificaciones", "Control local de preferencias; permiso push aún no solicitado", notifications) {
                notifications = it; prefs.edit().putBoolean("enable_notifications", it).apply()
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .22f))
            ChemDemoToggleRow("Avisos académicos push", "Tareas, clases y evaluaciones", push) {
                push = it; prefs.edit().putBoolean("push_academic", it).apply()
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .22f))
            ChemDemoToggleRow("Resumen por correo", "Resúmenes semanales", email) {
                email = it; prefs.edit().putBoolean("email_digest", it).apply()
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .22f))
            ChemDemoToggleRow("Recordatorios del Tutor IA", "Avisos sobre pendientes de estudio", aiReminder) {
                aiReminder = it; prefs.edit().putBoolean("ai_reminders", it).apply()
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .22f))
            ChemDemoToggleRow("Ofertas y promociones", "Preferencia de avisos promocionales", adverts) {
                adverts = it; prefs.edit().putBoolean("ad_notifications", it).apply()
            }
        }
        ChemMenuGroup {
            ChemMenuRow(Icons.Outlined.Forum, "Chat y comunicación", onClick = { onMockAction("Chat") })
            ChemMenuRow(Icons.Outlined.Folder, "Datos y almacenamiento", onClick = { onMockAction("Datos y almacenamiento") })
            if(!academicSpace) ChemMenuRow(Icons.Outlined.CreditCard, "Suscripción y facturación", onClick = { onOpen("membership") })
            ChemMenuRow(Icons.Outlined.Info, "Acerca de, términos y privacidad",
                onClick = { onMockAction("Acerca de") }, showDivider = false)
        }
    }
}
@Composable
private fun ChemDemoToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onChecked: (Boolean) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 13.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            if (subtitle.isNotBlank()) Text(subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        // Botón cuadrado compartido, sin Switch Material redondeado.
        ChemButton(if (checked) "Sí" else "No", { onChecked(!checked) },
            style = if (checked) ChemButtonStyle.FILLED else ChemButtonStyle.OUTLINED,
            height = ChemDimens.FilterHeight)
    }
}
