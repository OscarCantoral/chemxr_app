package com.example.ch3mxr.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Tamaños compartidos: no asignar alturas ni radios diferentes en cada pantalla. */
object ChemDimens {
    val ScreenHorizontalPadding = 24.dp

    val CornerRadius = 0.dp       // Todos los controles, paneles, tarjetas y selectores
    val ControlRadius = CornerRadius // Alias para el login anterior
    val ButtonHeight = 46.dp
    val ControlHeight = ButtonHeight // Alias de compatibilidad
    val InputHeight = 52.dp
    val FilterHeight = 36.dp
    val PanelPadding = 15.dp
    val PanelSpacing = 18.dp

    val TopBarHeight = 50.dp     // SOLO la barra, sin incluir la barra de estado Android
    val TopBarSearchHeight = 32.dp
    val TopBarAvatarSize = 32.dp
    val TopBarActionSize = 30.dp
    val TopBarHorizontalPadding = 12.dp
    val BottomBarHeight = 58.dp

    val LogoSize = 180.dp
    val BrandNameFontSize = 38.sp
    val BrandTaglineFontSize = 11.sp
}

object ChemShapes {
    val Component = RoundedCornerShape(ChemDimens.CornerRadius)
}
