package com.example.ch3mxr.feature.ads

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import com.example.ch3mxr.R
import com.example.ch3mxr.core.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class DemoAdCreative(
  @param:DrawableRes val resource: Int,
  val width: Int,
  val height: Int,
  val description: String,
) {
  val aspectRatio: Float
    get() = width.toFloat() / height
}

/** Recursos locales: no conecta SDK publicitario, red, seguimiento ni pagos. */
object DemoAds {
  val pro = DemoAdCreative(R.drawable.pub_00, 1031, 2048, "ChemXR PRO: beneficios del plan")
  val banner = DemoAdCreative(R.drawable.pub_01, 791, 151, "Publicidad de cursos Skillia")
  val agenda = DemoAdCreative(R.drawable.pub_02, 322, 702, "Skillia: aprende sin límites")
  val carousel =
    listOf(
      DemoAdCreative(R.drawable.pub_03, 1094, 720, "Skillia: cursos con 50 % de descuento"),
      DemoAdCreative(R.drawable.pub_04, 1094, 720, "Skillia Plus: prueba gratis"),
      DemoAdCreative(R.drawable.pub_05, 1094, 720, "Skillia: domina nuevas habilidades"),
    )
  val programs =
    DemoAdCreative(R.drawable.pub_06, 366, 500, "Impulsa tu carrera: programas profesionales")
  val domestika =
    DemoAdCreative(R.drawable.pub_07, 1180, 470, "Domestika: explora cursos creativos")
  val fashion =
    DemoAdCreative(R.drawable.pub_08, 1536, 1024, "Prendas de punto y zapatillas blancas")
  val inDrive =
    DemoAdCreative(
      R.drawable.pub_09,
      1254,
      1254,
      "inDrive: con o sin alta demanda, establece tu precio",
    )
}

private object AdBitmapCache {
  val images =
    object : LruCache<String, Bitmap>(20 * 1024 * 1024) {
      override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount
    }
}

/**
 * Decodifica fuera del hilo de UI y reutiliza bitmaps. drawable-nodpi evita escalado de densidad.
 */
@Composable
fun DemoAdImage(
  creative: DemoAdCreative,
  modifier: Modifier = Modifier,
  contentScale: ContentScale = ContentScale.Fit,
  alignment: Alignment = Alignment.Center,
) {
  val resources = LocalContext.current.resources
  val screenWidth =
    (LocalConfiguration.current.screenWidthDp * LocalDensity.current.density)
      .toInt()
      .coerceAtLeast(1)
  var sample = 1
  while (creative.width / (sample * 2) >= screenWidth) sample *= 2
  val cacheKey = "${creative.resource}:$sample"
  val bitmap by
    produceState<ImageBitmap?>(
      initialValue = AdBitmapCache.images.get(cacheKey)?.asImageBitmap(),
      key1 = cacheKey,
    ) {
      value =
        withContext(Dispatchers.IO) {
          val decoded =
            AdBitmapCache.images.get(cacheKey)
              ?: BitmapFactory.decodeResource(
                  resources,
                  creative.resource,
                  BitmapFactory.Options().apply {
                    inScaled = false
                    inSampleSize = sample
                  },
                )
                ?.also { AdBitmapCache.images.put(cacheKey, it) }
          decoded?.asImageBitmap()
        }
    }
  Box(modifier) {
    val image = bitmap
    if (image != null) {
      Image(
        image,
        creative.description,
        Modifier.fillMaxSize(),
        contentScale = contentScale,
        alignment = alignment,
      )
    } else {
      Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface))
    }
  }
}

@Composable
fun rememberDemoAdClick(): (String) -> Unit {
  val context = LocalContext.current
  return remember(context) {
    { advertiser: String ->
      Toast.makeText(context, "$advertiser · Anuncio de demostración", Toast.LENGTH_SHORT).show()
    }
  }
}
