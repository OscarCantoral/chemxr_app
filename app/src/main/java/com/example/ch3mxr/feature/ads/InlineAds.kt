package com.example.ch3mxr.feature.ads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.theme.ChemShapes
import kotlinx.coroutines.launch

/**
 * Cada página representa UN anuncio nativo independiente, con sus propios assets.
 * No es un formato de carrusel suministrado por AdMob. En producción la lista será
 * la de anuncios realmente cargados: puede tener 0, 1, 2 o 3 elementos.
 * Sin rotación automática, recargas por swipe ni reutilización de un NativeAd en dos vistas.
 */
@Composable
fun HomeAdCarousel(modifier: Modifier = Modifier) {
    if(com.example.ch3mxr.feature.learning.LocalLearningStore.current.isPro) return
    val ads = SampleNativeAds.home
    if (ads.isEmpty()) return
    val pager = rememberPagerState(pageCount = { ads.size })
    val scope = rememberCoroutineScope()
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        HorizontalPager(state = pager, key = { ads[it].id }, modifier = Modifier.fillMaxWidth()) { index ->
            SampleNativeAdCard(ads[index])
        }
        if (ads.size > 1) {
            // Navegación de la app fuera de la futura NativeAdView y separada del anuncio.
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                ads.indices.forEach { index ->
                    TextButton(onClick = { scope.launch { pager.scrollToPage(index) } }, shape = ChemShapes.Component,
                        contentPadding = PaddingValues(0.dp), modifier = Modifier.size(48.dp)) {
                        Text("${index + 1}", fontWeight = if (pager.currentPage == index) FontWeight.Bold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

/**
 * Maqueta del anchored adaptive banner, dentro del espacio reservado por Scaffold.
 * Sustituir el interior por AdView y su altura calculada por el SDK. Sin X superpuesta.
 * Los 16 dp son una decisión de diseño, NO una distancia oficial que garantice aprobación.
 */
@Composable
fun BottomAdBanner(modifier: Modifier = Modifier) {
    if(com.example.ch3mxr.feature.learning.LocalLearningStore.current.isPro) return
    val colors = MaterialTheme.colorScheme
    Column(modifier.fillMaxWidth().background(colors.background)) {
        Spacer(Modifier.height(16.dp))
        HorizontalDivider(color = colors.outline.copy(alpha = .35f))
        Column(Modifier.fillMaxWidth().background(colors.surface).padding(horizontal = 8.dp, vertical = 5.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Anuncio de prueba", style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant)
            DemoAdImage(DemoAds.banner, Modifier.fillMaxWidth().height(60.dp))
        }
        HorizontalDivider(color = colors.outline.copy(alpha = .35f))
        Spacer(Modifier.height(16.dp))
    }
}
