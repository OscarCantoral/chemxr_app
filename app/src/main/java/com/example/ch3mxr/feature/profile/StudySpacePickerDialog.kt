package com.example.ch3mxr.feature.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.components.ChemButton
import com.example.ch3mxr.core.ui.components.ChemButtonStyle
import com.example.ch3mxr.core.ui.components.ChemSelectablePanel
import com.example.ch3mxr.core.ui.theme.ChemDimens
import com.example.ch3mxr.core.ui.theme.ChemShapes
import com.example.ch3mxr.feature.profile.model.StudySpace

/** Selector de espacio: solo aparecen espacios de la lista disponible para el usuario. */
@Composable
fun StudySpacePickerDialog(
    spaces: List<StudySpace>,
    selectedId: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var pendingId by remember(selectedId, spaces) { mutableStateOf(selectedId) }
    val colors = MaterialTheme.colorScheme
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = ChemShapes.Component,
        containerColor = colors.surface,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("Tus espacios", style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold, color = colors.onSurface)
                Text("Selecciona dónde quieres aprender.", style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                spaces.forEach { space ->
                    val selected = pendingId == space.id
                    ChemSelectablePanel(
                        selected = selected,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { pendingId = space.id }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(if (space.academic) Icons.Outlined.School else Icons.Outlined.Person,
                                contentDescription = null, modifier = Modifier.size(22.dp))
                            Column(Modifier.weight(1f)) {
                                Text(space.name, fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodyMedium)
                                Text(space.description, style = MaterialTheme.typography.labelSmall)
                            }
                            if (selected) Icon(Icons.Outlined.Check, contentDescription = "Seleccionado")
                        }
                    }
                }
                Text("Las instituciones asignadas aparecerán aquí cuando conectemos la cuenta.",
                    style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant)
            }
        },
        dismissButton = {
            ChemButton("Cancelar", onDismiss,
                style = ChemButtonStyle.OUTLINED, height = ChemDimens.FilterHeight)
        },
        confirmButton = {
            ChemButton("Cambiar", {
                onSelect(pendingId)
                onDismiss()
            }, height = ChemDimens.FilterHeight)
        }
    )
}
