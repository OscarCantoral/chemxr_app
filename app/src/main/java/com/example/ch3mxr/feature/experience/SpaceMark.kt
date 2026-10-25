package com.example.ch3mxr.feature.experience

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.theme.LocalChemIsDark
import com.example.ch3mxr.feature.profile.model.StudySpace

/** Usa el logo institucional verdadero sin imponerle tintes ni fondos adicionales. */
@Composable
fun SpaceMark(space: StudySpace, size: Dp = 30.dp) {
    val logo = if (LocalChemIsDark.current) space.iconDark else space.iconLight
    if (logo != null) {
        Image(
            painter = painterResource(logo),
            contentDescription = "Logo de ${space.name}",
            modifier = Modifier.size(size),
            contentScale = ContentScale.Fit
        )
    } else {
        Box(
            Modifier.size(size).background(MaterialTheme.colorScheme.primary.copy(alpha = .15f)),
            contentAlignment = Alignment.Center
        ) {
            Text("CX", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}
