package com.example.ch3mxr.feature.ar

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.*
import androidx.lifecycle.compose.*
import com.google.ar.core.*
import io.github.sceneview.*
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.CancellationException

private tailrec fun Context.activity(): Activity? =
  when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.activity()
    else -> null
  }

@Composable
fun ArScreen(
  location: String,
  onFallback: () -> Unit,
  modifier: Modifier = Modifier,
  vm: ArViewModel = hiltViewModel(),
) {
  val context = LocalContext.current
  val owner = LocalLifecycleOwner.current
  val availability by vm.availability.collectAsStateWithLifecycle()
  var camera by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
        PackageManager.PERMISSION_GRANTED
    )
  }
  var message by remember { mutableStateOf<String?>(null) }
  var prepared by remember { mutableStateOf(false) }
  var preparationAttempt by remember { mutableIntStateOf(0) }
  LaunchedEffect(availability, camera, preparationAttempt) {
    prepared = false
    if (availability == ArCoreApk.Availability.SUPPORTED_INSTALLED && camera) {
      try {
        val activity = context.activity() ?: throw IllegalStateException("Activity unavailable")
        prepared =
          ArCoreApk.getInstance().requestInstall(activity, false) ==
            ArCoreApk.InstallStatus.INSTALLED
      } catch (e: Exception) {
        message = "No se pudo preparar la sesión de realidad aumentada."
      }
    }
  }

  val permission =
    rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
      camera = it
      if (!it) message = "La cámara es necesaria para mostrar realidad aumentada."
    }
  DisposableEffect(owner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        camera =
          ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        vm.refresh()
      }
    }
    owner.lifecycle.addObserver(observer)
    onDispose { owner.lifecycle.removeObserver(observer) }
  }
  if (availability == ArCoreApk.Availability.SUPPORTED_INSTALLED && camera && prepared) {
    ArSceneContent(location, modifier, onFallback)
  } else
    Column(modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
      when {
        availability == null || availability?.isTransient == true -> {
          Text("Comprobando compatibilidad…")
          CircularProgressIndicator()
          TextButton(onClick = vm::refresh) { Text("Comprobar de nuevo") }
        }
        availability?.isSupported != true -> {
          Text("Este dispositivo no tiene realidad aumentada disponible.")
          TextButton(onClick = vm::refresh) { Text("Reintentar") }
        }
        availability != ArCoreApk.Availability.SUPPORTED_INSTALLED -> {
          Text("Instala o actualiza Servicios de Google Play para RA.")
          Button(
            onClick = {
              try {
                val activity = context.activity() ?: return@Button
                val result = ArCoreApk.getInstance().requestInstall(activity, true)
                if (result == ArCoreApk.InstallStatus.INSTALLED) vm.refresh()
              } catch (e: Exception) {
                message = "No se pudo preparar la realidad aumentada. Puedes usar el visor 3D."
              }
            }
          ) {
            Text("Preparar realidad aumentada")
          }
        }
        camera && !prepared -> {
          Text(message ?: "Preparando realidad aumentada…")
          if (message == null) CircularProgressIndicator()
          else
            TextButton(
              onClick = {
                message = null
                preparationAttempt++
              }
            ) {
              Text("Reintentar")
            }
        }
        !camera -> {
          Text("Permite usar la cámara para colocar el modelo en tu entorno.")
          Button(onClick = { permission.launch(Manifest.permission.CAMERA) }) {
            Text("Permitir cámara")
          }
          TextButton(
            onClick = {
              context.startActivity(
                Intent(
                  Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                  Uri.parse("package:${context.packageName}"),
                )
              )
            }
          ) {
            Text("Abrir ajustes")
          }
        }
      }
      message?.let { Text(it) }
      TextButton(onClick = onFallback) { Text("Abrir visor 3D") }
    }
}

@Composable
private fun ArSceneContent(location: String, modifier: Modifier, onFallback: () -> Unit) {
  val engine = rememberEngine()
  val loader = rememberModelLoader(engine)
  val nodes = rememberNodes()
  val frame = remember { arrayOfNulls<Frame>(1) }
  var instance by remember(location) { mutableStateOf<ModelInstance?>(null) }
  var error by remember { mutableStateOf<String?>(null) }
  var placed by remember { mutableStateOf(false) }
  LaunchedEffect(location) {
    try {
      instance = loader.loadModelInstance(location)
      if (instance == null) error = "No se pudo cargar el modelo."
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      error = "No se pudo cargar el modelo."
    }
  }
  Box(modifier.fillMaxSize()) {
    if (error == null)
      ARScene(
        Modifier.fillMaxSize(),
        engine = engine,
        modelLoader = loader,
        childNodes = nodes,
        planeRenderer = !placed,
        sessionConfiguration = { _, config ->
          config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
        },
        onSessionFailed = { error = "No se pudo iniciar la cámara de realidad aumentada." },
        onSessionUpdated = { _, updated -> frame[0] = updated },
        onGestureListener =
          rememberOnGestureListener(
            onSingleTapConfirmed = { event, _ ->
              val model = instance
              if (!placed && model != null) {
                val hit =
                  frame[0]?.hitTest(event.x, event.y)?.firstOrNull {
                    it.isValid(depthPoint = false, point = false)
                  }
                val anchor = runCatching { hit?.createAnchor() }.getOrNull()
                if (anchor != null) {
                  nodes +=
                    AnchorNode(engine, anchor).apply {
                      addChildNode(
                        ModelNode(model, scaleToUnits = 0.35f).apply { isEditable = true }
                      )
                    }
                  placed = true
                }
              }
            }
          ),
      )
    Surface(Modifier.align(Alignment.TopCenter).padding(12.dp)) {
      Column(Modifier.padding(12.dp)) {
        Text(
          error
            ?: if (placed) "Modelo colocado. Puedes girarlo y ajustar su tamaño."
            else "Mueve el teléfono para detectar una superficie y tócala para colocar el modelo."
        )
        if (instance == null && error == null) CircularProgressIndicator()
        TextButton(onClick = onFallback) { Text("Usar visor 3D") }
      }
    }
  }
}
