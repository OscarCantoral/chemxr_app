package com.example.ch3mxr.feature.media

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ch3mxr.core.media.*
import com.example.ch3mxr.feature.ar.*
import java.io.File

@Composable
fun MediaResourcePage(
  id: String,
  spaceId: String,
  mode: String,
  onOpen: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val vm =
    hiltViewModel<ResourceViewModel, ResourceViewModel.Factory>(
      key = "resource:$spaceId:$id",
      creationCallback = { it.create(id, spaceId) },
    )
  val state by vm.state.collectAsStateWithLifecycle()
  val source = vm.source(state)
  val context = LocalContext.current
  when {
    state.loading -> CircularProgressIndicator()
    state.item == null || source == null -> Text(state.error ?: "Recurso no disponible.")
    mode == "pdf" -> PdfScreen(source, modifier)
    mode == "video" ->
      when (val video = VideoSources.parse(source)) {
        is VideoSource.Web -> WebVideoScreen(video, modifier)
        is VideoSource.File -> NativeVideoScreen(video.uri, modifier.fillMaxSize())
        null -> Text("Este enlace no contiene un video compatible. Revisa su ficha.")
      }
    mode in setOf("model", "ar") -> {
      var modelError by remember(source) { mutableStateOf<String?>(null) }
      val location by
        produceState<String?>(null, source) {
          try {
            value = vm.modelLocation(source)
          } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
          } catch (e: Exception) {
            modelError = "No se pudo abrir el modelo. Revisa el archivo o descarga una copia local."
          }
        }
      if (location == null) {
        if (modelError != null) Text(modelError!!) else CircularProgressIndicator()
      } else if (mode == "ar") ArScreen(location!!, { onOpen("media/model/$id") }, modifier)
      else ModelScreen(location!!, modifier)
    }
  }
}
