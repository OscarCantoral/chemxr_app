package com.example.ch3mxr.feature.experience

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ch3mxr.core.model.LoadState
import com.example.ch3mxr.core.ui.components.*
import com.example.ch3mxr.feature.library.catalog.LocalResourceImage
import com.example.ch3mxr.feature.main.components.*
import com.example.ch3mxr.feature.notes.NotesViewModel

/** Route uses lifecycle state; list items never perform synchronous file reads. */
@Composable
fun NotesPage(modifier: Modifier = Modifier, viewModel: NotesViewModel = hiltViewModel()) {
  val context = LocalContext.current
  val state by viewModel.state.collectAsStateWithLifecycle()
  val notes = (state as? LoadState.Ready)?.value.orEmpty()
  val saveError by viewModel.error.collectAsStateWithLifecycle()
  var draft by rememberSaveable { mutableStateOf("") }
  var photo by rememberSaveable { mutableStateOf("") }
  var saving by remember { mutableStateOf(false) }
  val picker =
    rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
      if (uri != null) {
        runCatching {
          context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION,
          )
        }
        photo = uri.toString()
      }
    }
  MainLazyPage(modifier) {
    item {
      Eyebrow("Mis apuntes · Privados")
      Heading("Mis apuntes.")
    }
    item {
      GroupPanel(
        "Crear nota",
        "Guardada en este dispositivo; la sincronización con API vendrá después",
      ) {
        OutlinedTextField(
          draft,
          { draft = it },
          label = { Text("Texto, referencias y enlaces") },
          modifier = Modifier.fillMaxWidth(),
          minLines = 4,
          enabled = !saving,
        )
        Spacer(Modifier.height(9.dp))
        ChemButton(
          "Adjuntar imagen",
          { picker.launch(arrayOf("image/*")) },
          Modifier.fillMaxWidth(),
          style = ChemButtonStyle.OUTLINED,
          leadingIcon = Icons.Outlined.AddPhotoAlternate,
          enabled = !saving,
        )
        if (photo.isNotBlank())
          Text(
            "Foto seleccionada: ${Uri.parse(photo).lastPathSegment?:"imagen"}",
            style = MaterialTheme.typography.labelSmall,
          )
        Spacer(Modifier.height(8.dp))
        ChemButton(
          if (saving) "Guardando…" else "Guardar apunte",
          {
            saving = true
            viewModel.add(draft, photo) {
              draft = ""
              photo = ""
              saving = false
            }
          },
          Modifier.fillMaxWidth(),
          enabled = !saving && (draft.isNotBlank() || photo.isNotBlank()),
        )
      }
    }
    if (state is LoadState.Loading) item { CircularProgressIndicator() }
    if (state is LoadState.Failed)
      item {
        Text((state as LoadState.Failed).message)
        TextButton(onClick = viewModel::retry) { Text("Reintentar") }
      }
    if (saveError != null) item { Text(saveError!!, color = MaterialTheme.colorScheme.error) }
    if (notes.isEmpty() && state is LoadState.Ready) item { Text("Todavía no tienes apuntes.") }
    itemsIndexed(notes, key = { _, note -> note.id }) { index, note ->
      GroupPanel("Apunte ${notes.size-index}", "Privado · Guardado en el teléfono") {
        if (note.text.isNotBlank()) Text(note.text)
        if (note.photoUri.isNotBlank()) LocalResourceImage(note.photoUri, "Imagen del apunte")
        Spacer(Modifier.height(8.dp))
        ChemButton(
          "Eliminar apunte",
          { viewModel.delete(note.id) },
          Modifier.fillMaxWidth(),
          style = ChemButtonStyle.OUTLINED,
        )
      }
    }
  }
  LaunchedEffect(saveError) { if (saveError != null) saving = false }
}
