package com.example.ch3mxr.feature.experience

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.theme.ChemShapes
import com.example.ch3mxr.core.ui.theme.LocalChemIsDark
import com.example.ch3mxr.feature.profile.model.StudySpace

/** Icono contextual configurable por institución y tema; vectores actuales son marcadores DEMO. */
@Composable
fun SpaceMark(space: StudySpace, size: Dp = 30.dp) {
    val isDark = LocalChemIsDark.current
    val image = if (isDark) space.iconDark else space.iconLight
    if (image != null) {
        Surface(shape = ChemShapes.Component, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha=.2f))) {
            Icon(painterResource(image), contentDescription = "Identidad de ${space.name}", tint = androidx.compose.ui.graphics.Color.Unspecified, modifier = Modifier.size(size))
        }
    } else {
        Box(Modifier.size(size).background(MaterialTheme.colorScheme.primary.copy(alpha = .15f)), contentAlignment = Alignment.Center) {
            Text("CX", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}
