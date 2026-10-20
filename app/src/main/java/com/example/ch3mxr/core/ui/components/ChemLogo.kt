package com.example.ch3mxr.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.example.ch3mxr.R
import com.example.ch3mxr.core.ui.theme.ChemDimens
import com.example.ch3mxr.core.ui.theme.LocalChemIsDark

/** Selecciona la imagen segun el tema, pero no cambia su tamano. */
@Composable
fun ChemLogo(
    modifier: Modifier = Modifier,
    size: Dp = ChemDimens.LogoSize
) {
    val resId = if (LocalChemIsDark.current) R.drawable.logo else R.drawable.logo_2
    Image(
        painter = painterResource(resId),
        contentDescription = stringResource(R.string.app_name),
        modifier = modifier.size(size),
        contentScale = ContentScale.Fit
    )
}
