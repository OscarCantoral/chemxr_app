package com.example.ch3mxr.feature.library.catalog

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ch3mxr.core.model.*
import com.example.ch3mxr.core.ui.components.*
import com.example.ch3mxr.core.ui.theme.ChemShapes
import com.example.ch3mxr.feature.experience.*
import com.example.ch3mxr.feature.learning.*
import com.example.ch3mxr.feature.main.components.*
import com.example.ch3mxr.feature.profile.model.StudySpace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ResourceLibraryPage(
  space: StudySpace,
  modifier: Modifier = Modifier,
  onOpen: (String) -> Unit,
) {
  val store = LocalLearning.current
  var query by rememberSaveable(space.id) { mutableStateOf("") }
  var kind by rememberSaveable(space.id) { mutableStateOf("Todos") }
  var category by rememberSaveable(space.id) { mutableStateOf("Todas") }
  val scoped = store.resources.filter { it.spaceId == space.id }
  val filtered =
    scoped.filter {
      (kind == "Todos" || it.kind.name == kind) &&
        (category == "Todas" || it.category == category) &&
        (query.isBlank() ||
          "${it.title} ${it.author} ${it.tags} ${it.category}".contains(query, true))
    }
  MainLazyPage(modifier.navigationBarsPadding()) {
    item {
      SpaceContextHeader(
        space,
        if (space.academic) "Biblioteca institucional" else "Biblioteca abierta y colaborativa",
      )
    }
    item {
      Heading(if (space.academic) "Biblioteca de ${space.name}." else "Biblioteca de Mi espacio.")
    }
    item {
      Text("Recursos registrados por tipo, tema y fuente. Mi estante guarda tus accesos favoritos.")
    }
    item {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ChemButton("Registrar recurso", { onOpen("submitResource") }, Modifier.weight(1f))
        ChemButton(
          "Mi estante",
          { onOpen("shelf") },
          Modifier.weight(1f),
          style = ChemButtonStyle.OUTLINED,
        )
      }
    }
    item {
      OutlinedTextField(
        query,
        { query = it },
        label = { Text("Buscar título, autor, tema o etiquetas") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
      )
    }
    item {
      Row(
        Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        FilterChip(
          selected = kind == "Todos",
          onClick = { kind = "Todos" },
          label = { Text("Todos") },
          shape = ChemShapes.Component,
        )
        ResourceKind.entries.forEach { x ->
          FilterChip(
            selected = kind == x.name,
            onClick = { kind = x.name },
            label = { Text(x.label) },
            shape = ChemShapes.Component,
          )
        }
      }
    }
    item {
      Row(
        Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        (listOf("Todas") + scoped.map { it.category }.distinct().sorted()).forEach { x ->
          FilterChip(
            selected = category == x,
            onClick = { category = x },
            label = { Text(x) },
            shape = ChemShapes.Component,
          )
        }
      }
    }
    item { Text("${filtered.size} recursos", style = MaterialTheme.typography.labelLarge) }
    if (filtered.isEmpty())
      item { Text("No hay recursos para estos filtros. Puedes registrar un archivo o enlace.") }
    items(filtered, key = { it.id }) { resource -> ResourceCatalogCard(resource, onOpen) }
  }
}

@Composable
fun ResourceCatalogCard(resource: CatalogResource, onOpen: (String) -> Unit) {
  val store = LocalLearning.current
  GroupPanel(resource.title, "${resource.kind.label} · ${resource.category}") {
    if (resource.kind == ResourceKind.IMAGE && resource.fileUri.isNotBlank())
      LocalResourceImage(resource.fileUri, "Vista del recurso")
    Text("Por ${resource.author}", style = MaterialTheme.typography.bodySmall)
    if (resource.tags.isNotBlank()) Text(resource.tags, style = MaterialTheme.typography.labelSmall)
    Text(
      resource.origin,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      ChemButton("Ver recurso", { onOpen("resource/${resource.id}") }, Modifier.weight(1f))
      ChemButton(
        if (store.isSaved("resource:${resource.id}")) "Guardado" else "Guardar",
        { store.toggleShortcut(resourceShortcut(resource)) },
        Modifier.weight(1f),
        style = ChemButtonStyle.OUTLINED,
      )
    }
  }
}

fun resourceShortcut(resource: CatalogResource) =
  SavedShortcut(
    "resource:${resource.id}",
    resource.spaceId,
    resource.title,
    "Recurso",
    "resource/${resource.id}",
  )

@Composable
fun ResourceDetailPage(
  id: String,
  space: StudySpace,
  modifier: Modifier = Modifier,
  onOpen: (String) -> Unit = {},
) {
  val store = LocalLearning.current
  val context = LocalContext.current
  val vm =
    androidx.hilt.navigation.compose.hiltViewModel<
      com.example.ch3mxr.feature.media.ResourceViewModel,
      com.example.ch3mxr.feature.media.ResourceViewModel.Factory,
    >(
      key = "resource:${space.id}:$id",
      creationCallback = { it.create(id, space.id) },
    )
  val resourceState by vm.state.collectAsStateWithLifecycle()
  val item = resourceState.item
  MainPage(modifier.navigationBarsPadding()) {
    if (resourceState.loading) {
      CircularProgressIndicator()
      return@MainPage
    }
    if (item == null) {
      Heading("Recurso no disponible.")
      return@MainPage
    }
    SpaceContextHeader(space, "Ficha de biblioteca")
    Heading(item.title)
    DemoBadge(item.kind.label)
    if (item.kind == ResourceKind.IMAGE && item.fileUri.isNotBlank())
      LocalResourceImage(item.fileUri, "Imagen del recurso")
    SimpleInfo("Tema", item.category)
    SimpleInfo("Autor / Fuente", item.author)
    SimpleInfo("Licencia / uso", item.license)
    if (item.tags.isNotBlank()) Text("Etiquetas: ${item.tags}")
    Text("Registrado desde: ${item.origin}", style = MaterialTheme.typography.bodySmall)
    if (item.url.isNotBlank()) Text(item.url, style = MaterialTheme.typography.bodySmall)
    val source = vm.source(resourceState).orEmpty()
    val local =
      source.startsWith("asset:///") ||
        source.startsWith("content://") ||
        source.startsWith("private:///")
    when (item.kind) {
      ResourceKind.PDF ->
        if (local)
          ChemButton("Leer PDF", { onOpen("media/pdf/${item.id}") }, Modifier.fillMaxWidth())
      ResourceKind.VIDEO,
      ResourceKind.AUDIO ->
        ChemButton("Reproducir aquí", { onOpen("media/video/${item.id}") }, Modifier.fillMaxWidth())
      ResourceKind.MODEL_3D ->
        if (local) {
          ChemButton(
            "Abrir visor 3D",
            { onOpen("media/model/${item.id}") },
            Modifier.fillMaxWidth(),
          )
          ChemButton(
            "Ver en realidad aumentada",
            { onOpen("media/ar/${item.id}") },
            Modifier.fillMaxWidth(),
            style = ChemButtonStyle.OUTLINED,
          )
        }
      else -> Unit
    }
    val offline = resourceState.offline
    if (
      offline?.state == com.example.ch3mxr.feature.library.offline.domain.DownloadState.DOWNLOADING
    ) {
      LinearProgressIndicator(Modifier.fillMaxWidth())
      TextButton(onClick = vm::cancel) { Text("Cancelar descarga") }
    } else if (
      offline?.state == com.example.ch3mxr.feature.library.offline.domain.DownloadState.AVAILABLE
    ) {
      Text("Disponible sin conexión")
      TextButton(onClick = vm::delete) { Text("Eliminar copia descargada") }
    } else if (
      !item.fileUri.startsWith("asset:///") &&
        item.fileUri.isBlank() &&
        item.kind in
          setOf(ResourceKind.PDF, ResourceKind.MODEL_3D, ResourceKind.VIDEO, ResourceKind.AUDIO) &&
        com.example.ch3mxr.core.media.VideoSources.parse(item.url) !is
          com.example.ch3mxr.core.media.VideoSource.Web
    ) {
      TextButton(onClick = vm::download) { Text("Descargar para usar sin conexión") }
    }
    resourceState.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    if (!item.fileUri.startsWith("asset:///"))
      ChemButton(
        if (item.fileUri.isNotBlank()) "Abrir archivo" else "Abrir enlace",
        {
          val intent =
            if (item.fileUri.isNotBlank())
              Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(Uri.parse(item.fileUri), item.mime.ifBlank { "*/*" })
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
              }
            else Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
          runCatching { context.startActivity(intent) }
            .onFailure {
              Toast.makeText(
                  context,
                  "No se pudo abrir. Revisa el acceso al archivo o instala una app compatible.",
                  Toast.LENGTH_LONG,
                )
                .show()
            }
        },
        Modifier.fillMaxWidth(),
      )
    ChemButton(
      if (store.isSaved("resource:${item.id}")) "Quitar de Mi estante" else "Guardar en Mi estante",
      { store.toggleShortcut(resourceShortcut(item)) },
      Modifier.fillMaxWidth(),
      style = ChemButtonStyle.OUTLINED,
    )
  }
}

@Composable
fun PersonalShelfPage(modifier: Modifier = Modifier, onOpen: (String) -> Unit) {
  val store = LocalLearning.current
  var filter by rememberSaveable { mutableStateOf("Todos") }
  val visible = store.shortcuts.filter { filter == "Todos" || it.kind == filter }
  MainLazyPage(modifier.navigationBarsPadding()) {
    item {
      Eyebrow("Favoritos personales")
      Heading("Mi estante.")
    }
    item {
      Text(
        "Accesos directos a cursos, recursos, publicaciones y comentarios. Guardarlos aquí conserva su ubicación original."
      )
    }
    item {
      Row(
        Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        listOf("Todos", "Curso", "Recurso", "Publicación", "Comentario").forEach { value ->
          FilterChip(
            selected = filter == value,
            onClick = { filter = value },
            label = { Text(value) },
            shape = ChemShapes.Component,
          )
        }
      }
    }
    if (visible.isEmpty()) item { Text("Todavía no tienes favoritos en esta categoría.") }
    items(visible, key = { it.id }) { shortcut ->
      GroupPanel(
        shortcut.title,
        "${shortcut.kind} · ${if(shortcut.spaceId=="personal") "Mi espacio" else shortcut.spaceId.uppercase()}",
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          ChemButton(
            "Abrir",
            { onOpen("switchSpace/${shortcut.spaceId}/${shortcut.route}") },
            Modifier.weight(1f),
          )
          ChemButton(
            "Quitar",
            { store.toggleShortcut(shortcut) },
            Modifier.weight(1f),
            style = ChemButtonStyle.OUTLINED,
          )
        }
      }
    }
    item {
      DemoNavRow(
        "Mis apuntes privados",
        "Notas propias, fuera de la biblioteca compartida",
        Icons.Outlined.EditNote,
      ) {
        onOpen("notes")
      }
    }
  }
}

@Composable
fun RegisterResourcePage(
  space: StudySpace,
  modifier: Modifier = Modifier,
  onOpen: (String) -> Unit,
) {
  var created by remember { mutableStateOf<CatalogResource?>(null) }
  MainPage(modifier.navigationBarsPadding()) {
    SpaceContextHeader(space, "Registrar en biblioteca")
    Heading("Comparte un recurso.")
    if (created == null) ResourceEditor(space.id, "Biblioteca") { created = it }
    else {
      DemoBadge("Registrado en la biblioteca de ${space.name}")
      ChemButton("Ver ficha", { onOpen("resource/${created!!.id}") }, Modifier.fillMaxWidth())
      ChemButton(
        "Registrar otro",
        { created = null },
        Modifier.fillMaxWidth(),
        style = ChemButtonStyle.OUTLINED,
      )
    }
  }
}

@Composable
fun ResourceEditorDialog(
  spaceId: String,
  onDismiss: () -> Unit,
  onRegistered: (CatalogResource) -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    shape = ChemShapes.Component,
    title = { Text("Registrar recurso") },
    text = {
      Column(Modifier.heightIn(max = 430.dp).verticalScroll(rememberScrollState())) {
        ResourceEditor(spaceId, "Comunidad", onRegistered)
      }
    },
    confirmButton = {},
    dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
  )
}

@Composable
private fun ResourceEditor(
  spaceId: String,
  origin: String,
  onRegistered: (CatalogResource) -> Unit,
) {
  val store = LocalLearning.current
  val context = LocalContext.current
  var title by rememberSaveable { mutableStateOf("") }
  var author by rememberSaveable { mutableStateOf("") }
  var category by rememberSaveable { mutableStateOf("") }
  var tags by rememberSaveable { mutableStateOf("") }
  var license by rememberSaveable { mutableStateOf("") }
  var url by rememberSaveable { mutableStateOf("") }
  var file by rememberSaveable { mutableStateOf("") }
  var mime by rememberSaveable { mutableStateOf("") }
  var kind by rememberSaveable { mutableStateOf(ResourceKind.LINK) }
  var error by rememberSaveable { mutableStateOf("") }
  var menu by remember { mutableStateOf(false) }
  val picker =
    rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
      if (uri != null) {
        runCatching {
          context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION,
          )
        }
        file = uri.toString()
        mime = context.contentResolver.getType(uri).orEmpty()
        url = ""
        val displayName =
          runCatching {
              context.contentResolver
                .query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                ?.use { cursor -> if (cursor.moveToFirst()) cursor.getString(0) else null }
            }
            .getOrNull()
        if (title.isBlank()) title = displayName ?: "Recurso adjunto"
        kind =
          when {
            mime == "application/pdf" -> ResourceKind.PDF
            mime.startsWith("image/") -> ResourceKind.IMAGE
            mime.startsWith("video/") -> ResourceKind.VIDEO
            mime.startsWith("audio/") -> ResourceKind.AUDIO
            mime.contains("presentation") -> ResourceKind.SLIDES
            mime.contains("sheet") -> ResourceKind.SHEET
            mime.startsWith("model/") -> ResourceKind.MODEL_3D
            else -> ResourceKind.OTHER
          }
        error = ""
      }
    }
  Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
    OutlinedTextField(
      title,
      { title = it },
      label = { Text("Título") },
      modifier = Modifier.fillMaxWidth(),
    )
    Box {
      ChemButton(
        "Tipo: ${kind.label}",
        { menu = true },
        Modifier.fillMaxWidth(),
        style = ChemButtonStyle.OUTLINED,
      )
      DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
        ResourceKind.entries.forEach { x ->
          DropdownMenuItem(
            text = { Text(x.label) },
            onClick = {
              kind = x
              menu = false
            },
          )
        }
      }
    }
    OutlinedTextField(
      category,
      { category = it },
      label = { Text("Tema / Categoría") },
      modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
      author,
      { author = it },
      label = { Text("Autor / Fuente") },
      modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
      url,
      {
        url = it
        file = ""
        mime = ""
      },
      label = { Text("Enlace: Drive, Scribd u otro sitio") },
      modifier = Modifier.fillMaxWidth(),
      singleLine = true,
    )
    ChemButton(
      if (file.isBlank()) "Seleccionar archivo" else "Cambiar archivo seleccionado",
      { picker.launch(arrayOf("*/*")) },
      Modifier.fillMaxWidth(),
      style = ChemButtonStyle.OUTLINED,
    )
    if (file.isNotBlank()) Text("Archivo seleccionado", style = MaterialTheme.typography.labelSmall)
    OutlinedTextField(
      tags,
      { tags = it },
      label = { Text("Etiquetas, separadas por comas") },
      modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
      license,
      { license = it },
      label = { Text("Licencia o permiso de uso") },
      modifier = Modifier.fillMaxWidth(),
    )
    if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
    ChemButton(
      "Registrar en biblioteca",
      {
        if (title.isBlank() || category.isBlank() || author.isBlank() || license.isBlank())
          error = "Completa título, tema, autor y licencia o permiso."
        else {
          val item =
            CatalogResource(
              uid(),
              spaceId,
              title.trim(),
              kind,
              category.trim(),
              author.trim(),
              tags.trim(),
              license.trim(),
              url.trim(),
              file,
              mime,
              origin,
            )
          store.registerResource(item) { saved ->
            if (saved) onRegistered(item)
            else error = "Selecciona un archivo o escribe un enlace http(s) válido."
          }
        }
      },
      Modifier.fillMaxWidth(),
    )
    Text(
      "Se conserva la referencia y el acceso que permite la fuente. Los enlaces externos mantienen sus condiciones de acceso.",
      style = MaterialTheme.typography.labelSmall,
    )
  }
}

/** Miniaturas fuera del hilo principal; permiso de lectura persistente para documentos elegidos. */
@Composable
fun LocalResourceImage(uri: String, description: String) {
  val context = LocalContext.current
  val bitmap by
    produceState<ImageBitmap?>(null, uri) {
      value =
        withContext(Dispatchers.IO) {
          runCatching {
              context.contentResolver.openInputStream(Uri.parse(uri))?.use { stream ->
                BitmapFactory.decodeStream(
                    stream,
                    null,
                    BitmapFactory.Options().apply { inSampleSize = 4 },
                  )
                  ?.asImageBitmap()
              }
            }
            .getOrNull()
        }
    }
  if (bitmap != null) Image(bitmap!!, description, Modifier.fillMaxWidth().heightIn(max = 250.dp))
  else
    Text(
      "Vista previa no disponible. Puedes abrir el archivo.",
      style = MaterialTheme.typography.labelSmall,
    )
}

/** Adjuntos de comunidad: siempre elige una ficha existente o la registra primero. */
@Composable
fun ResourceAttachmentPicker(spaceId: String, selectedId: String, onSelected: (String) -> Unit) {
  val store = LocalLearning.current
  var choosing by remember { mutableStateOf(false) }
  var registering by remember { mutableStateOf(false) }
  Column {
    Box {
      TextButton(onClick = { choosing = true }) { Text("Adjuntar recurso de biblioteca") }
      DropdownMenu(expanded = choosing, onDismissRequest = { choosing = false }) {
        store.resources
          .filter { it.spaceId == spaceId }
          .forEach { item ->
            DropdownMenuItem(
              text = { Text(item.title) },
              onClick = {
                onSelected(item.id)
                choosing = false
              },
            )
          }
      }
    }
    TextButton(onClick = { registering = true }) { Text("Registrar archivo o enlace nuevo") }
    val selected = store.resource(selectedId, spaceId)
    if (selected != null) {
      Text(selected.title, style = MaterialTheme.typography.bodySmall)
      TextButton(onClick = { onSelected("") }) { Text("Quitar adjunto") }
    }
  }
  if (registering)
    ResourceEditorDialog(spaceId, { registering = false }) { item ->
      onSelected(item.id)
      registering = false
    }
}
