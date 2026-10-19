package com.example.ch3mxr.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.ch3mxr.R
import com.example.ch3mxr.core.ui.theme.LocalChemIsDark

@Composable
fun ChemLogo(modifier: Modifier = Modifier) {
    // Ambas imágenes deben tener el fondo transparente.
    val logoRes = if (LocalChemIsDark.current) R.drawable.logo else R.drawable.logo_2

    Image(
        painter = painterResource(id = logoRes),
        contentDescription = "ChemXR",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}
