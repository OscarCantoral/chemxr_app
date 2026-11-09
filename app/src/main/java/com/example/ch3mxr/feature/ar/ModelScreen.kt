package com.example.ch3mxr.feature.ar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import io.github.sceneview.*
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.CancellationException

@Composable
fun ModelScreen(location: String, modifier: Modifier = Modifier) {
  val engine = rememberEngine()
  val loader = rememberModelLoader(engine)
  val nodes = rememberNodes()
  val camera = rememberCameraNode(engine) { position = Position(0f, 0f, 2.5f) }
  var loading by remember(location) { mutableStateOf(true) }
  var error by remember(location) { mutableStateOf<String?>(null) }
  var retry by remember { mutableIntStateOf(0) }
  LaunchedEffect(location, retry) {
    loading = true
    error = null
    try {
      val model = loader.loadModelInstance(location) ?: throw IllegalStateException("Invalid model")
      nodes += ModelNode(model, scaleToUnits = 1.3f)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      error = "No se pudo cargar el modelo 3D."
    } finally {
      loading = false
    }
  }
  Box(modifier.fillMaxSize()) {
    Scene(
      Modifier.fillMaxSize(),
      engine = engine,
      modelLoader = loader,
      cameraNode = camera,
      childNodes = nodes,
    )
    Column(Modifier.align(Alignment.TopCenter).padding(12.dp)) {
      Text(
        "Arrastra para girar · Usa dos dedos para acercar",
        color = MaterialTheme.colorScheme.primary,
      )
      Text(
        "Esquema didáctico: tamaños y distancias no están a escala.",
        style = MaterialTheme.typography.bodySmall,
      )
      if (loading) CircularProgressIndicator()
      error?.let {
        Text(it)
        TextButton(onClick = { retry++ }) { Text("Reintentar") }
      }
    }
  }
}
