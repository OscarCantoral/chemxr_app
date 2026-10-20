package com.example.ch3mxr.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.ch3mxr.R
import com.example.ch3mxr.core.ui.theme.ChemDimens

/** Cabecera de marca reusable. Ninguna cadena de marca esta escrita directamente en Kotlin. */
@Composable
fun ChemBrand(
    modifier: Modifier = Modifier,
    logoSize: Dp = ChemDimens.LogoSize,
    showTagline: Boolean = true
) {
    val appName = stringResource(R.string.app_name)
    val accentSuffix = stringResource(R.string.app_name_accent_suffix)
    val tagline = stringResource(R.string.app_tagline)
    val hasSuffix = accentSuffix.isNotBlank() && appName.endsWith(accentSuffix, ignoreCase = true)
    val nameBase = if (hasSuffix) appName.dropLast(accentSuffix.length) else appName

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        ChemLogo(modifier = Modifier.height(logoSize), size = logoSize)
        Spacer(Modifier.height(4.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                    append(nameBase)
                }
                if (hasSuffix) {
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append(appName.takeLast(accentSuffix.length))
                    }
                }
            },
            fontWeight = FontWeight.Bold,
            fontSize = ChemDimens.BrandNameFontSize,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
        if (showTagline && tagline.isNotBlank()) {
            Spacer(Modifier.height(7.dp))
            Text(
                text = tagline,
                fontSize = ChemDimens.BrandTaglineFontSize,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}
