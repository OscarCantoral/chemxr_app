package com.example.ch3mxr.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.ch3mxr.R
import com.example.ch3mxr.core.ui.theme.LocalChemIsDark

/** Icono vectorial de dos capas. Mantiene la geometría sin PNG y sin degradados.
 * En tema oscuro: blanco y el color primario elegido por el usuario.
 * En tema claro: monocromático oscuro, como el anterior logoia_2.png.
 */
@Composable
fun ChemAiLogo(
    modifier: Modifier = Modifier,
    contentDescription: String? = "Tutor IA"
) {
    val colors = MaterialTheme.colorScheme
    val baseColor = colors.onBackground
    val accentColor = if (LocalChemIsDark.current) colors.primary else baseColor

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Icon(
            painter = painterResource(R.drawable.ic_chem_ai_base),
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            tint = baseColor
        )
        Icon(
            painter = painterResource(R.drawable.ic_chem_ai_accent),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            tint = accentColor
        )
    }
}
