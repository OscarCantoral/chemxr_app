package com.example.ch3mxr.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import com.example.ch3mxr.core.ui.theme.ChemDimens

/** Única excepción circular al diseño rectangular: el avatar de usuario. */
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
        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.35f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Perfil",
                modifier = Modifier.size(avatarSize * .66f),
                tint = colors.primary
            )
        }
    }
}
