package com.example.ch3mxr.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
    onMockAction: (String) -> Unit
) {
    var largeText by rememberSaveable { mutableStateOf(false) }
    var reduceMotion by rememberSaveable { mutableStateOf(false) }
    var notifications by rememberSaveable { mutableStateOf(true) }
    var aiBottom by rememberSaveable { mutableStateOf(true) }

    MainPage(modifier) {
        Eyebrow("A tu manera")
        Heading("Tu experiencia,\ntus preferencias.", 27)
        Eyebrow("Cuenta")
        ChemMenuGroup {
            ChemMenuRow(Icons.Outlined.Person, "Cuenta y datos personales", onClick = { onMockAction("Cuenta") })
            ChemMenuRow(Icons.Outlined.Lock, "Privacidad", onClick = { onMockAction("Privacidad") })
            ChemMenuRow(Icons.Outlined.VpnKey, "Seguridad y cuentas vinculadas",
                onClick = { onMockAction("Seguridad") }, showDivider = false)
        }
        Eyebrow("Experiencia")
        ChemPanel {
            Text("Apariencia", color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(9.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                listOf(ThemeMode.SYSTEM to "Sistema", ThemeMode.LIGHT to "Claro", ThemeMode.DARK to "Oscuro")
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
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                ChemAccent.entries.forEach { a ->
                    ChemButton(a.title, { onAccentChange(a) }, Modifier.weight(1f),
                        style = if (accent == a) ChemButtonStyle.FILLED else ChemButtonStyle.OUTLINED,
                        height = ChemDimens.FilterHeight)
                }
            }
            Spacer(Modifier.height(19.dp))
            ChemDemoToggleRow("Texto más grande", "Mejora la lectura", largeText) { largeText = it }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .22f))
            ChemDemoToggleRow("Reducir movimiento", "", reduceMotion) { reduceMotion = it }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .22f))
            Spacer(Modifier.height(13.dp))
            Text("Abrir Tutor IA como", style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChemButton("Panel inferior", { aiBottom = true }, Modifier.weight(1f),
                    style = if (aiBottom) ChemButtonStyle.FILLED else ChemButtonStyle.OUTLINED,
                    height = ChemDimens.FilterHeight)
                ChemButton("Pantalla", { aiBottom = false }, Modifier.weight(1f),
                    style = if (!aiBottom) ChemButtonStyle.FILLED else ChemButtonStyle.OUTLINED,
                    height = ChemDimens.FilterHeight)
            }
            Spacer(Modifier.height(15.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .22f))
            ChemDemoToggleRow("Recordatorios en la app", "", notifications) { notifications = it }
        }
        ChemMenuGroup {
            ChemMenuRow(Icons.Outlined.Forum, "Chat y comunicación", onClick = { onMockAction("Chat") })
            ChemMenuRow(Icons.Outlined.Folder, "Datos y almacenamiento", onClick = { onMockAction("Datos y almacenamiento") })
            ChemMenuRow(Icons.Outlined.CreditCard, "Suscripción y facturación", onClick = { onMockAction("Suscripción") })
            ChemMenuRow(Icons.Outlined.Info, "Acerca de, términos y privacidad",
                onClick = { onMockAction("Acerca de") }, showDivider = false)
        }
        Eyebrow("Laboratorio del prototipo")
        ChemPanel {
            Text("Estos controles simulan opciones locales. No son permisos ni políticas del servidor.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(18.dp))
            ChemButton("Probar cuenta gratuita", { onMockAction("Escenario gratuito") },
                modifier = Modifier.fillMaxWidth(), style = ChemButtonStyle.OUTLINED)
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
