package com.example.ch3mxr.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.example.ch3mxr.core.ui.theme.ChemDimens

/** Logo vectorial y pulsación sin ripple / animación de botón. */
@Composable
fun ChemAiButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .size(ChemDimens.TopBarActionSize)
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        ChemAiLogo(modifier = Modifier.size(ChemDimens.TopBarAiLogoSize))
    }
}
