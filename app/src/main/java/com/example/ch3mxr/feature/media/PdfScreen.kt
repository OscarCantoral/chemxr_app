package com.example.ch3mxr.feature.media

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PdfScreen(source: String, modifier: Modifier = Modifier) {
  val vm =
    hiltViewModel<PdfViewModel, PdfViewModel.Factory>(
      key = "pdf:$source",
      creationCallback = { it.create(source) },
    )
  val state by vm.state.collectAsStateWithLifecycle()
  var zoom by remember { mutableFloatStateOf(1f) }
  var offsetX by remember { mutableFloatStateOf(0f) }
  var offsetY by remember { mutableFloatStateOf(0f) }
  LaunchedEffect(vm) { vm.open() }
  DisposableEffect(vm) { onDispose { vm.release() } }
  Column(modifier.fillMaxSize().padding(12.dp)) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      TextButton(
        onClick = {
          vm.page(state.page - 1)
          zoom = 1f
          offsetX = 0f
          offsetY = 0f
        },
        enabled = state.page > 0 && !state.loading,
      ) {
        Text("Anterior")
      }
      Text(if (state.pages > 0) "${state.page+1} / ${state.pages}" else "PDF")
      TextButton(
        onClick = {
          vm.page(state.page + 1)
          zoom = 1f
          offsetX = 0f
          offsetY = 0f
        },
        enabled = state.page + 1 < state.pages && !state.loading,
      ) {
        Text("Siguiente")
      }
    }
    TextButton(
      onClick = {
        zoom = 1f
        offsetX = 0f
        offsetY = 0f
      }
    ) {
      Text("Ajustar página")
    }
    if (state.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
    state.error?.let {
      Text(it)
      TextButton(
        onClick = {
          vm.release()
          vm.open()
        }
      ) {
        Text("Reintentar")
      }
    }
    Box(
      Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).pointerInput(Unit) {
        detectTransformGestures { _, pan, change, _ ->
          offsetX += pan.x
          offsetY += pan.y
          zoom = (zoom * change).coerceIn(1f, 3f)
        }
      }
    ) {
      state.bitmap?.let {
        Image(
          it.asImageBitmap(),
          "Página ${state.page+1}",
          Modifier.fillMaxWidth().graphicsLayer {
            scaleX = zoom
            scaleY = zoom
            translationX = offsetX
            translationY = offsetY
          },
        )
      }
    }
  }
}
