package com.example.ch3mxr.feature.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.components.*
import com.example.ch3mxr.core.ui.theme.*
import com.example.ch3mxr.feature.profile.model.StudySpace
import com.example.ch3mxr.feature.experience.SpaceMark

@Composable
fun StudySpacePickerDialog(
    spaces: List<StudySpace>, selectedId: String,
    onSelect: (String) -> Unit, onDismiss: () -> Unit
) {
    var pendingId by remember(selectedId) { mutableStateOf(selectedId) }
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = ChemShapes.Component,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Cambiar de espacio", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("La selección se conserva cuando cierres la app.", style = MaterialTheme.typography.labelSmall)
                spaces.forEach { space ->
                    ChemSelectablePanel(selected = pendingId == space.id, modifier = Modifier.fillMaxWidth(), onClick = { pendingId = space.id }) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (space.academic) SpaceMark(space, 36.dp)
                            else ChemAvatarImage(avatarSize = 36.dp)
                            Column(Modifier.weight(1f)) {
                                Text(space.name, fontWeight = FontWeight.Bold)
                                Text(space.description, style = MaterialTheme.typography.labelSmall)
                            }
                            if (pendingId == space.id) Icon(Icons.Outlined.Check, "Seleccionado")
                        }
                    }
                }
                Text("UTP y Senati son espacios ficticios de demostración; no acreditan matrículas ni afiliaciones.", style = MaterialTheme.typography.labelSmall)
            }
        },
        dismissButton = { ChemButton("Cancelar", onDismiss, style = ChemButtonStyle.OUTLINED, height = ChemDimens.FilterHeight) },
        confirmButton = { ChemButton("Cambiar", { onSelect(pendingId); onDismiss() }, height = ChemDimens.FilterHeight) }
    )
}
