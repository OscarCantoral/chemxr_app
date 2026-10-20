package com.example.ch3mxr.feature.main.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ch3mxr.core.ui.components.ChemButton
import com.example.ch3mxr.core.ui.components.ChemButtonStyle
import com.example.ch3mxr.core.ui.components.ChemIconTile
import com.example.ch3mxr.core.ui.components.ChemPanel
import com.example.ch3mxr.core.ui.components.ChemProgress
import com.example.ch3mxr.core.ui.theme.ChemDimens
import com.example.ch3mxr.feature.main.model.DemoCourse

@Composable
fun MainPage(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
            .padding(horizontal = 15.dp).padding(top = 22.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(ChemDimens.PanelSpacing),
        content = content
    )
}

@Composable
fun Eyebrow(text: String) {
    Text(text.uppercase(), color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
}

@Composable
fun Heading(text: String, fontSize: Int = 27) {
    Text(text, style = MaterialTheme.typography.headlineMedium.copy(
        fontSize = fontSize.sp, fontWeight = FontWeight.ExtraBold, lineHeight = (fontSize+3).sp),
        color = MaterialTheme.colorScheme.onBackground)
}

@Composable
fun SectionHeading(text: String, action: String = "", onAction: () -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f))
        if (action.isNotEmpty()) Text(action, modifier = Modifier.clickable(onClick = onAction),
            fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
    }
}

/** Todos los filtros comparten el botón ChemButton, no crean otro botón Material3. */
@Composable
fun DemoFilterButton(text: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    ChemButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        height = ChemDimens.FilterHeight,
        style = if (selected) ChemButtonStyle.FILLED else ChemButtonStyle.OUTLINED
    )
}

@Composable
fun DemoPanel(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    ChemPanel(modifier = modifier, content = content)
}

@Composable
fun IconTile(icon: ImageVector, background: Color, foreground: Color, size: Int = 76) {
    ChemIconTile(icon, background, foreground, size)
}

@Composable
fun ProgressStrip(progress: Float, modifier: Modifier = Modifier) {
    ChemProgress(progress, modifier)
}

@Composable
fun CourseCard(course: DemoCourse, modifier: Modifier = Modifier) {
    DemoPanel(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            IconTile(course.icon, course.tileColor, course.iconColor, 76)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(course.area, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(course.title, fontSize = 15.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface, lineHeight = 18.sp, maxLines = 2)
                Text(course.subtitle, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                if (course.progress > 0f || course.isPrivate) {
                    ProgressStrip(course.progress, Modifier.fillMaxWidth())
                    Text("${(course.progress * 100).toInt()} % completado",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
