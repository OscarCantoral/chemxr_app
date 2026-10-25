package com.example.ch3mxr.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.example.ch3mxr.R
import com.example.ch3mxr.core.ui.theme.ChemDimens
import com.example.ch3mxr.core.ui.theme.LocalChemIsDark

/**
 * El logo oscuro se dibuja a partir de DOS vectores con la geometría exacta del
 * logo.xml original. Solo la segunda figura toma el acento seleccionado.
 *
 * En tema claro se respeta logo_2 (totalmente negro, sin tinte institucional).
 * No se necesita modificar los colores del VectorDrawable durante la ejecución.
 */
@Composable
fun ChemLogo(
    modifier: Modifier = Modifier,
    size: Dp = ChemDimens.LogoSize
) {
    val dark = LocalChemIsDark.current
    val accent = MaterialTheme.colorScheme.primary
    val description = stringResource(R.string.app_name)

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        if (dark) {
            Icon(
                painter = painterResource(R.drawable.chem_logo_base),
                contentDescription = description,
                modifier = Modifier.fillMaxSize(),
                tint = Color(0xFFF5F5F5)
            )
            Icon(
                painter = painterResource(R.drawable.chem_logo_accent),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                tint = accent
            )
        } else {
            Image(
                painter = painterResource(R.drawable.logo_2),
                contentDescription = description,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Color.Black)
            )
        }
    }
}
