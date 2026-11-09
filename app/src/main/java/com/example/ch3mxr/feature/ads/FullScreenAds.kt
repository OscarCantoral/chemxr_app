package com.example.ch3mxr.feature.ads

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ch3mxr.core.model.*
import com.example.ch3mxr.core.ui.theme.ChemLime
import com.example.ch3mxr.core.ui.theme.ChemShapes

private val ProBackground = Color(0xFF051117)

/** Promoción PRO propia de ChemXR. NO es AdMob ni un anuncio App Open. */
@Composable
fun ProWelcomeScreen(onPro: () -> Unit, onSkip: () -> Unit) {
  FullscreenAdSystemBars()
  BackHandler(onBack = onSkip)
  Column(Modifier.fillMaxSize().background(ProBackground)) {
    // Fit conserva todos los textos; TopCenter fija el arte al borde superior.
    DemoAdImage(DemoAds.pro, Modifier.fillMaxWidth().weight(1f), alignment = Alignment.TopCenter)
    Column(
      Modifier.fillMaxWidth()
        .background(ProBackground)
        .navigationBarsPadding()
        .padding(horizontal = 20.dp)
        .padding(top = 8.dp, bottom = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Surface(
        onClick = onPro,
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
        shape = ChemShapes.Component,
        color = ChemLime,
        contentColor = ProBackground,
      ) {
        Box(
          Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
          contentAlignment = Alignment.Center,
        ) {
          Text("Pásate a PRO", fontSize = 23.sp, fontWeight = FontWeight.Bold)
        }
      }
      TextButton(
        onClick = onSkip,
        modifier = Modifier.heightIn(min = 48.dp),
        shape = ChemShapes.Component,
      ) {
        Text("Por ahora no", color = Color(0xFFD5DDE1), fontSize = 14.sp)
      }
    }
  }
}

/**
 * Simulación de intersticial después de guardar. La actividad ya está registrada. Sustituir TODO
 * este Dialog por el intersticial del SDK: el SDK decide los controles, cierre y temporización. No
 * reutilizar los hotspots ni dibujar una X sobre un anuncio real.
 */
@Composable
fun AgendaAdDialog(onClose: () -> Unit) {
  if (com.example.ch3mxr.feature.learning.LocalLearning.current.isPro) return
  Dialog(
    onDismissRequest = onClose,
    properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
  ) {
    FullscreenAdSystemBars()
    val onAdClick = rememberDemoAdClick()
    Column(Modifier.fillMaxSize().background(Color(0xFF08052D))) {
      Row(
        Modifier.fillMaxWidth().statusBarsPadding().padding(start = 16.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          "Anuncio de prueba",
          color = Color.White,
          style = MaterialTheme.typography.labelMedium,
          modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onClose, modifier = Modifier.size(48.dp)) {
          Icon(Icons.Outlined.Close, "Cerrar anuncio y volver a la agenda", tint = Color.White)
        }
      }
      BoxWithConstraints(Modifier.fillMaxWidth().weight(1f).navigationBarsPadding()) {
        val imageWidth = minOf(maxWidth, maxHeight * DemoAds.agenda.aspectRatio)
        val imageHeight = imageWidth / DemoAds.agenda.aspectRatio
        Box(Modifier.size(imageWidth, imageHeight).align(Alignment.TopCenter)) {
          DemoAdImage(DemoAds.agenda, Modifier.fillMaxSize())
          // Áreas táctiles sobre los dos textos ya dibujados en pub_02.
          Box(
            Modifier.offset(x = imageWidth * .025f, y = imageHeight * .86f)
              .width(imageWidth * .95f)
              .height(imageHeight * .075f)
              .semantics { contentDescription = "Comenzar ahora con Skillia" }
              .clickable(role = Role.Button) { onAdClick("Skillia") }
          )
          Box(
            Modifier.offset(x = imageWidth * .28f, y = imageHeight * .94f)
              .width(imageWidth * .44f)
              .height(imageHeight * .055f)
              .semantics { contentDescription = "No, gracias. Volver a la agenda" }
              .clickable(role = Role.Button, onClick = onClose)
          )
        }
      }
    }
  }
}

/**
 * Aplica el modo inmersivo solo mientras se muestra la promoción, y restaura la ventana al salir.
 */
@Composable
private fun FullscreenAdSystemBars() {
  val view = LocalView.current
  val context = LocalContext.current
  val window = (view.parent as? DialogWindowProvider)?.window ?: context.activity()?.window
  DisposableEffect(window, view) {
    val controller = window?.let { WindowCompat.getInsetsController(it, view) }
    val previousBehavior = controller?.systemBarsBehavior
    controller?.apply {
      systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
      hide(WindowInsetsCompat.Type.systemBars())
    }
    onDispose {
      controller?.show(WindowInsetsCompat.Type.systemBars())
      if (previousBehavior != null) controller?.systemBarsBehavior = previousBehavior
    }
  }
}

private tailrec fun Context.activity(): Activity? =
  when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.activity()
    else -> null
  }
