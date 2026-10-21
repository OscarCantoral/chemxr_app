package com.example.ch3mxr.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.ch3mxr.R
import com.example.ch3mxr.core.ui.theme.ChemDimens
import com.example.ch3mxr.core.ui.theme.LocalChemIsDark

/** Botón de Tutor IA: sólo imagen transparente, sin borde ni rectángulo. */
@Composable
fun ChemAiButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageRes = if (LocalChemIsDark.current) R.drawable.logoia else R.drawable.logoia_2

    Box(
        modifier = modifier
            .size(ChemDimens.TopBarActionSize)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Tutor IA",
            modifier = Modifier.size(ChemDimens.TopBarAiLogoSize),
            contentScale = ContentScale.Fit
        )
    }
}
