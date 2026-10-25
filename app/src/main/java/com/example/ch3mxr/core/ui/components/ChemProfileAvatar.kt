package com.example.ch3mxr.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.R
import com.example.ch3mxr.core.ui.theme.ChemDimens

/** Foto local provisional: res/drawable/foto.jpg. Luego podrá venir del perfil de la API. */
@Composable
fun ChemAvatarImage(
    modifier: Modifier = Modifier,
    avatarSize: Dp = ChemDimens.TopBarAvatarSize
) {
    Image(
        painter = painterResource(R.drawable.foto),
        contentDescription = "Foto de perfil",
        modifier = modifier.size(avatarSize).clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

/** Avatar circular compartido por la barra superior y el perfil del estudiante. */
@Composable
fun ChemProfileAvatar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    avatarSize: Dp = ChemDimens.TopBarAvatarSize
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        modifier = modifier.size(avatarSize),
        shape = CircleShape,
        color = colors.surface,
        border = BorderStroke(1.dp, colors.outline.copy(alpha = .35f))
    ) {
        ChemAvatarImage(modifier = Modifier.fillMaxSize(), avatarSize = avatarSize)
    }
}
