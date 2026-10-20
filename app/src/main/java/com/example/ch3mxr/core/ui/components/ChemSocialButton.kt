package com.example.ch3mxr.core.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import com.example.ch3mxr.core.ui.theme.ChemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.theme.ChemDimens

/** Solo visual; no inicia OAuth hasta integrar el modulo de autenticacion. */
@Composable
fun ChemSocialButton(
    label: String,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.widthIn(min = 115.dp).height(ChemDimens.ButtonHeight),
        shape = ChemShapes.Component,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.Center) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = Color.Unspecified, // Conservar colores originales Google/Facebook.
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(label, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold)
        }
    }
}
