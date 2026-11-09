package com.example.ch3mxr.feature.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.model.*
import com.example.ch3mxr.core.ui.components.*
import com.example.ch3mxr.core.ui.theme.ChemShapes
import com.example.ch3mxr.feature.ads.NativeAdPlacement
import com.example.ch3mxr.feature.ads.NativeAdSlot
import com.example.ch3mxr.feature.experience.*
import com.example.ch3mxr.feature.learning.*
import com.example.ch3mxr.feature.library.catalog.ResourceAttachmentPicker
import com.example.ch3mxr.feature.library.catalog.ResourceCatalogCard
import com.example.ch3mxr.feature.library.catalog.ResourceEditorDialog
import com.example.ch3mxr.feature.main.components.*
import com.example.ch3mxr.feature.profile.model.StudySpace

/** Un voto por contenido: +1, -1 o sin voto. Pulsar otra vez retira el voto. */
@Composable
fun VoteControls(contentKey: String, baseScore: Int, modifier: Modifier = Modifier) {
  val store = LocalLearning.current
  val current = store.votes[contentKey] ?: 0
  Row(modifier, verticalAlignment = Alignment.CenterVertically) {
    IconButton(onClick = { store.vote(contentKey, 1) }, modifier = Modifier.size(48.dp)) {
      Icon(
        Icons.Outlined.ArrowUpward,
        if (current == 1) "Retirar voto positivo" else "Votar positivo",
        tint =
          if (current == 1) MaterialTheme.colorScheme.primary
          else MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    Text(
      "${baseScore+current}",
      fontWeight = FontWeight.Bold,
      modifier = Modifier.widthIn(min = 24.dp),
    )
    IconButton(onClick = { store.vote(contentKey, -1) }, modifier = Modifier.size(48.dp)) {
      Icon(
        Icons.Outlined.ArrowDownward,
        if (current == -1) "Retirar voto negativo" else "Votar negativo",
        tint =
          if (current == -1) MaterialTheme.colorScheme.primary
          else MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@Composable
fun ContentOptions(
  contentKey: String,
  title: String,
  spaceId: String,
  kind: String,
  route: String,
) {
  val store = LocalLearning.current
  var expanded by remember { mutableStateOf(false) }
  var reporting by remember { mutableStateOf(false) }
  var reason by remember { mutableStateOf("Spam o publicidad indebida") }
  var sent by remember { mutableStateOf(false) }
  Box {
    IconButton(onClick = { expanded = true }, modifier = Modifier.size(48.dp)) {
      Icon(Icons.Outlined.MoreVert, "Opciones de $kind")
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      DropdownMenuItem(
        text = {
          Text(if (store.isSaved(contentKey)) "Quitar de Mi estante" else "Guardar en Mi estante")
        },
        onClick = {
          store.toggleShortcut(SavedShortcut(contentKey, spaceId, title, kind, route))
          expanded = false
        },
      )
      DropdownMenuItem(
        text = { Text("Ocultar para mí") },
        onClick = {
          store.hide(contentKey)
          expanded = false
        },
      )
      DropdownMenuItem(
        text = { Text("Denunciar / Reportar") },
        onClick = {
          expanded = false
          reporting = true
          sent = false
        },
      )
    }
  }
  if (reporting)
    AlertDialog(
      onDismissRequest = { reporting = false },
      shape = ChemShapes.Component,
      title = { Text(if (sent) "Reporte registrado" else "Reportar contenido") },
      text = {
        if (sent) Text("Tu reporte quedó registrado en esta demostración.")
        else
          Column {
            listOf(
                "Spam o publicidad indebida",
                "Contenido ofensivo",
                "Información engañosa",
                "Otro motivo",
              )
              .forEach { value ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                  RadioButton(selected = reason == value, onClick = { reason = value })
                  Text(value)
                }
              }
          }
      },
      confirmButton = {
        TextButton(
          onClick = {
            if (sent) reporting = false
            else {
              store.report(contentKey, reason)
              sent = true
            }
          }
        ) {
          Text(if (sent) "Cerrar" else "Registrar reporte")
        }
      },
      dismissButton = {
        if (!sent) TextButton(onClick = { reporting = false }) { Text("Cancelar") }
      },
    )
}

@Composable
fun CommunityFeedScreen(
  space: StudySpace,
  modifier: Modifier = Modifier,
  onOpen: (String) -> Unit,
) {
  val store = LocalLearning.current
  val visible =
    store.posts.filter {
      it.spaceId == space.id && !it.group.startsWith("Foro ·") && "post:${it.id}" !in store.hidden
    }
  MainLazyPage(modifier.navigationBarsPadding()) {
    item { SpaceContextHeader(space, "Preguntas, recursos y colaboración") }
    item { Heading("Aprendamos en comunidad.") }
    item {
      DemoNavRow("Tus comunidades", "Explora grupos y sus reglas", Icons.Outlined.Groups) {
        onOpen("communityList")
      }
    }
    item { CommunityComposer(space.id) }
    item { SectionHeading("Publicaciones") }
    if (visible.isEmpty()) item { Text("Todavía no hay publicaciones visibles.") }
    visible.forEach { post ->
      item(key = post.id) { CommunityPostCard(post, onOpen) }
      if (post.id == "lucia" && store.adsAllowed(space.id))
        item(key = "community_ad") { NativeAdSlot(NativeAdPlacement.COMMUNITY) }
    }
    if (store.hidden.isNotEmpty())
      item { TextButton(onClick = { store.showHidden() }) { Text("Mostrar contenido oculto") } }
    item {
      DemoNavRow(
        "Biblioteca del espacio",
        "Todos los recursos compartidos, ordenados por tipo y tema",
        Icons.Outlined.LocalLibrary,
      ) {
        onOpen("library")
      }
    }
    item {
      DemoNavRow("Canales", "Comunicados y contenido de autores", Icons.Outlined.Campaign) {
        onOpen("channels")
      }
    }
    item {
      DemoNavRow("Chats académicos", "Conversaciones y equipos", Icons.AutoMirrored.Outlined.Chat) {
        onOpen("messages")
      }
    }
  }
}

@Composable
private fun CommunityComposer(spaceId: String) {
  val store = LocalLearning.current
  var text by rememberSaveable(spaceId) { mutableStateOf("") }
  var resourceId by rememberSaveable(spaceId) { mutableStateOf("") }
  var choosing by remember { mutableStateOf(false) }
  var registering by remember { mutableStateOf(false) }
  var error by remember { mutableStateOf("") }
  val attached = store.resource(resourceId, spaceId)
  GroupPanel("Crear publicación", "Comparte una pregunta, una idea o un recurso registrado") {
    OutlinedTextField(
      text,
      { text = it },
      label = { Text("¿Qué quieres compartir?") },
      minLines = 3,
      modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(8.dp))
    Box {
      ChemButton(
        "Adjuntar desde biblioteca",
        { choosing = true },
        Modifier.fillMaxWidth(),
        style = ChemButtonStyle.OUTLINED,
      )
      DropdownMenu(expanded = choosing, onDismissRequest = { choosing = false }) {
        store.resources
          .filter { it.spaceId == spaceId }
          .forEach { item ->
            DropdownMenuItem(
              text = { Text(item.title) },
              onClick = {
                resourceId = item.id
                choosing = false
              },
            )
          }
      }
    }
    TextButton(onClick = { registering = true }) { Text("Registrar un archivo o enlace nuevo") }
    if (attached != null) {
      Text("Recurso: ${attached.title}", style = MaterialTheme.typography.bodySmall)
      TextButton(onClick = { resourceId = "" }) { Text("Quitar adjunto") }
    }
    if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
    ChemButton(
      "Publicar",
      {
        store.publish(spaceId, text, resourceId) { saved ->
          if (saved) {
            text = ""
            resourceId = ""
            error = ""
          } else error = "Revisa el mensaje y registra sus enlaces en la biblioteca."
        }
      },
      Modifier.fillMaxWidth(),
    )
  }
  if (registering)
    ResourceEditorDialog(spaceId, { registering = false }) { item ->
      resourceId = item.id
      registering = false
    }
}

@Composable
fun CommunityPostCard(post: CommunityPost, onOpen: (String) -> Unit, showComments: Boolean = true) {
  val store = LocalLearning.current
  ChemPanel {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(9.dp),
    ) {
      AuthorAvatar(post.initials)
      Column(Modifier.weight(1f)) {
        Text(post.author, fontWeight = FontWeight.Bold)
        Text(post.group, style = MaterialTheme.typography.labelSmall)
      }
      ContentOptions(
        "post:${post.id}",
        post.text.take(70).ifBlank { "Publicación de ${post.author}" },
        post.spaceId,
        "Publicación",
        "discussion/${post.id}",
      )
    }
    Spacer(Modifier.height(12.dp))
    if (post.text.isNotBlank()) Text(post.text)
    val resource = store.resource(post.resourceId, post.spaceId)
    if (resource != null) {
      Spacer(Modifier.height(10.dp))
      ResourceCatalogCard(resource, onOpen)
    }
    Row(
      Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      VoteControls("post:${post.id}", post.baseScore)
      if (showComments)
        TextButton(onClick = { onOpen("discussion/${post.id}") }, shape = ChemShapes.Component) {
          Text("${store.replies.count {it.postId==post.id}} comentarios")
        }
    }
  }
}

@Composable
fun DiscussionScreen(
  postId: String,
  space: StudySpace,
  modifier: Modifier = Modifier,
  onOpen: (String) -> Unit,
) {
  val store = LocalLearning.current
  val post = store.posts.firstOrNull { it.id == postId && it.spaceId == space.id }
  var draft by rememberSaveable(postId) { mutableStateOf("") }
  var attached by rememberSaveable(postId) { mutableStateOf("") }
  var replyError by rememberSaveable(postId) { mutableStateOf("") }
  val comments = store.replies.filter { it.postId == postId && "comment:${it.id}" !in store.hidden }
  MainLazyPage(modifier.navigationBarsPadding()) {
    if (post == null) {
      item { Heading("Publicación no disponible.") }
      return@MainLazyPage
    }
    item { CommunityPostCard(post, onOpen, showComments = false) }
    item { Heading("Comentarios.") }
    item {
      ChemPanel {
        OutlinedTextField(
          draft,
          { draft = it },
          label = { Text("Escribe un comentario") },
          modifier = Modifier.fillMaxWidth(),
          minLines = 2,
        )
        ResourceAttachmentPicker(space.id, attached) { attached = it }
        if (replyError.isNotBlank()) Text(replyError, color = MaterialTheme.colorScheme.error)
        ChemButton(
          "Comentar",
          {
            store.reply(post.id, draft, attached) { saved ->
              if (saved) {
                draft = ""
                attached = ""
                replyError = ""
              } else replyError = "Revisa el comentario y sus recursos adjuntos."
            }
          },
          Modifier.fillMaxWidth(),
        )
      }
    }
    comments.forEachIndexed { index, comment ->
      item(key = comment.id) { CommunityReplyCard(comment, post.spaceId, onOpen) }
      if (index == 0 && post.id == "lucia" && store.adsAllowed(space.id))
        item(key = "comment_ad") { NativeAdSlot(NativeAdPlacement.COMMENTS) }
    }
    if (comments.isEmpty()) item { Text("Sé el primero en comentar.") }
    if (store.hidden.isNotEmpty())
      item { TextButton(onClick = { store.showHidden() }) { Text("Mostrar contenido oculto") } }
  }
}

@Composable
private fun CommunityReplyCard(reply: CommunityReply, spaceId: String, onOpen: (String) -> Unit) {
  val store = LocalLearning.current
  val key = "comment:${reply.id}"
  Column {
    Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
      AuthorAvatar(reply.initials)
      Column(Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(reply.author, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
          ContentOptions(
            key,
            "Comentario de ${reply.author}",
            spaceId,
            "Comentario",
            "discussion/${reply.postId}",
          )
        }
        if (reply.text.isNotBlank()) Text(reply.text)
        val resource = store.resource(reply.resourceId, spaceId)
        if (resource != null) ResourceCatalogCard(resource, onOpen)
        VoteControls(key, reply.baseScore)
      }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .3f))
  }
}

@Composable
private fun AuthorAvatar(initials: String) {
  Box(
    Modifier.size(38.dp)
      .background(MaterialTheme.colorScheme.primary.copy(alpha = .15f), CircleShape),
    contentAlignment = Alignment.Center,
  ) {
    Text(initials, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
  }
}

/** El foro institucional usa los mismos votos, adjuntos y menús que la comunidad. */
@Composable
fun CourseForumScreen(
  courseId: String,
  space: StudySpace,
  modifier: Modifier = Modifier,
  onOpen: (String) -> Unit,
) {
  val store = LocalLearning.current
  val course = ClassroomCatalog.find(courseId)?.takeIf { it.spaceId == space.id }
  if (course == null) {
    MainPage(modifier) { Heading("Foro no disponible.") }
    return
  }
  val id = "forum_${course.id}"
  LaunchedEffect(course.id) { store.ensureForum(course) }
  if (store.posts.any { it.id == id }) DiscussionScreen(id, space, modifier, onOpen)
  else MainPage(modifier) { Text("Preparando foro…") }
}
