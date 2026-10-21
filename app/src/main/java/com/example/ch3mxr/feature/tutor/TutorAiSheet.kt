package com.example.ch3mxr.feature.tutor

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.OpenInFull
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.example.ch3mxr.R
import com.example.ch3mxr.core.ui.components.ChemButton
import com.example.ch3mxr.core.ui.components.ChemIconAction
import com.example.ch3mxr.core.ui.components.ChemButtonStyle
import com.example.ch3mxr.core.ui.theme.ChemDimens
import com.example.ch3mxr.core.ui.theme.ChemShapes
import com.example.ch3mxr.core.ui.theme.LocalChemIsDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorAiSheet(contextLabel: String, onDismiss: () -> Unit) {
    var fullscreen by rememberSaveable { mutableStateOf(false) }
    var question by rememberSaveable { mutableStateOf("") }
    var lastQuestion by rememberSaveable { mutableStateOf("") }
    val c = MaterialTheme.colorScheme
    val image = if (LocalChemIsDark.current) R.drawable.logoia else R.drawable.logoia_2
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = ChemShapes.Component,
        containerColor = c.background,
        dragHandle = null
    ) {
        Column(modifier = Modifier.fillMaxWidth()
            .fillMaxHeight(if (fullscreen) 0.96f else 0.76f)
            .padding(horizontal = 17.dp, vertical = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Image(painterResource(image), contentDescription = "Tutor IA",
                    modifier = Modifier.size(38.dp), contentScale = ContentScale.Fit)
                Column(Modifier.weight(1f)) {
                    Text("Tutor IA", style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold, color = c.onBackground)
                    Text("Tu compañero de aprendizaje", style = MaterialTheme.typography.labelSmall,
                        color = c.onSurfaceVariant)
                }
                ChemIconAction(Icons.Outlined.OpenInFull, "Cambiar tamaño", { fullscreen = !fullscreen })
                ChemIconAction(Icons.Outlined.Close, "Cerrar", onDismiss)
            }
            Spacer(Modifier.height(15.dp))
            Surface(shape = ChemShapes.Component, color = c.surface) {
                Text("Contexto: $contextLabel", modifier = Modifier.fillMaxWidth().padding(12.dp),
                    color = c.primary, style = MaterialTheme.typography.labelMedium)
            }
            Spacer(Modifier.height(15.dp))
            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                Surface(color = c.surface, shape = ChemShapes.Component) {
                    Text("¡Hola! Puedo ayudarte a explorar ChemXR. Por ahora mis respuestas son simuladas.",
                        modifier = Modifier.padding(15.dp), color = c.onSurface)
                }
                if (lastQuestion.isNotBlank()) {
                    Spacer(Modifier.height(12.dp))
                    Text("Tú: $lastQuestion", color = c.primary)
                    Spacer(Modifier.height(12.dp))
                    Surface(color = c.surface, shape = ChemShapes.Component) {
                        Text("Este es el prototipo visual del tutor. Todavía no hay conexión con un modelo de IA.",
                            modifier = Modifier.padding(15.dp), color = c.onSurface)
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChemButton("¿Dónde estoy?", { lastQuestion = "¿Dónde estoy?" },
                    style = ChemButtonStyle.OUTLINED, height = ChemDimens.FilterHeight)
                ChemButton("Hazme una pregunta", { question = "¿Qué puedo aprender aquí?" },
                    style = ChemButtonStyle.OUTLINED, height = ChemDimens.FilterHeight)
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(question, onValueChange = { question = it },
                    modifier = Modifier.weight(1f).height(ChemDimens.TutorInputHeight),
                    placeholder = { Text("Pregunta lo que necesites...", style = MaterialTheme.typography.bodySmall) },
                    singleLine = true, shape = ChemShapes.Component)
                ChemIconAction(Icons.Outlined.Send, "Enviar", {
                    if (question.isNotBlank()) { lastQuestion = question; question = "" }
                }, tint = c.primary)
            }
            Text("Demo local · Respuestas simuladas, sin conexión a IA.",
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                color = c.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
        }
    }
}
