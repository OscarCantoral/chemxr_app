package com.example.ch3mxr.feature.ads

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.model.*
import com.example.ch3mxr.core.ui.components.ChemButton
import com.example.ch3mxr.core.ui.theme.ChemShapes

/** Una ubicación estable por bloque; asignar un ad unit ID propio al integrar AdMob. */
enum class NativeAdPlacement {
  COURSES,
  EXPLORE,
  COMMUNITY,
  COMMENTS,
}

/** Solo muestras locales. En producción estos campos provienen del anuncio, incluido el CTA. */
internal data class SampleNativeAd(
  val id: String,
  val advertiser: String,
  val headline: String,
  val body: String,
  val callToAction: String,
  val media: DemoAdCreative,
  val iconText: String,
)

internal object SampleNativeAds {
  val home =
    listOf(
      SampleNativeAd(
        "home_03",
        "Skillia",
        "Aprende sin límites",
        "Cursos de IA, Excel, inglés y diseño para avanzar a tu ritmo.",
        "Ver cursos",
        DemoAds.carousel[0],
        "S",
      ),
      SampleNativeAd(
        "home_04",
        "Skillia Plus",
        "Descubre Skillia Plus",
        "Explora cursos y nuevas habilidades con una prueba del plan Plus.",
        "Probar ahora",
        DemoAds.carousel[1],
        "S",
      ),
      SampleNativeAd(
        "home_05",
        "Skillia",
        "Habilidades para tu carrera",
        "Aprende Excel, IA, inglés y marketing con cursos prácticos.",
        "Ver cursos",
        DemoAds.carousel[2],
        "S",
      ),
    )

  fun forPlacement(placement: NativeAdPlacement): SampleNativeAd =
    when (placement) {
      NativeAdPlacement.COURSES ->
        SampleNativeAd(
          "courses_07",
          "Domestika",
          "Aprende nuevas habilidades",
          "Cursos de diseño, fotografía, ilustración y creatividad.",
          "Ver cursos",
          DemoAds.domestika,
          "D",
        )
      NativeAdPlacement.EXPLORE ->
        SampleNativeAd(
          "explore_06",
          "Formación Profesional",
          "Impulsa tu carrera",
          "Conoce programas de gestión, tecnología, idiomas y datos.",
          "Ver programas",
          DemoAds.programs,
          "FP",
        )
      NativeAdPlacement.COMMUNITY ->
        SampleNativeAd(
          "community_08",
          "Lino & Co.",
          "Zapatillas y prendas para tu día a día",
          "Descubre una colección para acompañarte cada día.",
          "Comprar",
          DemoAds.fashion,
          "LC",
        )
      NativeAdPlacement.COMMENTS ->
        SampleNativeAd(
          "comments_09",
          "inDrive",
          "Viaja a tu precio",
          "Elige tu destino y propón el precio de tu viaje.",
          "Instalar",
          DemoAds.inDrive,
          "iD",
        )
    }
}

/**
 * Punto de integración: sustituir el renderer demo por NativeAdView + MediaView. No convertir los
 * PNG en anuncios de Google ni trasladar clics manuales al SDK. Si no hay anuncio disponible,
 * omitir el bloque sin bloquear el contenido.
 */
@Composable
fun NativeAdSlot(placement: NativeAdPlacement, modifier: Modifier = Modifier) {
  if (com.example.ch3mxr.feature.learning.LocalLearning.current.isPro) return
  SampleNativeAdCard(
    SampleNativeAds.forPlacement(placement),
    modifier,
    threadLayout = placement == NativeAdPlacement.COMMENTS,
  )
}

@Composable
internal fun SampleNativeAdCard(
  ad: SampleNativeAd,
  modifier: Modifier = Modifier,
  threadLayout: Boolean = false,
) {
  val colors = MaterialTheme.colorScheme
  val demoClick = rememberDemoAdClick()
  var showInformation by remember(ad.id) { mutableStateOf(false) }
  // El contenedor y sus espacios vacíos no son clicables.
  Surface(
    modifier.fillMaxWidth(),
    shape = ChemShapes.Component,
    color = colors.surface,
    border = BorderStroke(1.dp, colors.outline.copy(alpha = .45f)),
  ) {
    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Surface(
          color = colors.onSurface,
          contentColor = colors.surface,
          shape = ChemShapes.Component,
        ) {
          Text(
            "Anuncio de prueba",
            Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
          )
        }
        Spacer(Modifier.weight(1f))
        // Reserva visual para AdChoices. Este icono informativo NO es AdChoices real.
        IconButton(onClick = { showInformation = true }, modifier = Modifier.size(48.dp)) {
          Icon(Icons.Outlined.Info, "Información del anuncio de prueba")
        }
      }
      if (threadLayout) {
        AdvertiserIdentity(ad)
        Text(
          ad.headline,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
        )
        Text(ad.body, style = MaterialTheme.typography.bodyMedium)
      }
      BoxWithConstraints(Modifier.fillMaxWidth()) {
        // Fit conserva la pieza completa; el futuro MediaView acepta imagen o vídeo.
        val mediaHeight = (maxWidth / ad.media.aspectRatio).coerceIn(120.dp, 340.dp)
        DemoAdImage(ad.media, Modifier.fillMaxWidth().height(mediaHeight))
      }
      if (!threadLayout) {
        AdvertiserIdentity(ad)
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              ad.headline,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
            )
            Text(
              ad.body,
              style = MaterialTheme.typography.bodySmall,
              color = colors.onSurfaceVariant,
            )
          }
          ChemButton(ad.callToAction, { demoClick(ad.advertiser) }, Modifier.width(112.dp))
        }
      } else {
        ChemButton(ad.callToAction, { demoClick(ad.advertiser) }, Modifier.align(Alignment.End))
      }
    }
  }
  if (showInformation) {
    AlertDialog(
      onDismissRequest = { showInformation = false },
      shape = ChemShapes.Component,
      title = { Text("Anuncio de prueba") },
      text = {
        Text("Esta es una muestra de ${ad.advertiser}. No abre una tienda ni realiza compras.")
      },
      confirmButton = { TextButton(onClick = { showInformation = false }) { Text("Entendido") } },
    )
  }
}

@Composable
private fun AdvertiserIdentity(ad: SampleNativeAd) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    // Iniciales solo para la maqueta; mostrar el icono real si lo entrega el SDK.
    Box(
      Modifier.size(38.dp)
        .background(MaterialTheme.colorScheme.primary.copy(alpha = .15f), CircleShape),
      contentAlignment = Alignment.Center,
    ) {
      Text(ad.iconText, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
    Text(
      ad.advertiser,
      style = MaterialTheme.typography.labelLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}
