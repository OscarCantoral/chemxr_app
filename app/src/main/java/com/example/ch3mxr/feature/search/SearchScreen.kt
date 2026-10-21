package com.example.ch3mxr.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.components.ChemIconAction
import com.example.ch3mxr.core.ui.components.ChemMenuGroup
import com.example.ch3mxr.core.ui.components.ChemMenuRow
import com.example.ch3mxr.core.ui.theme.ChemDimens
import com.example.ch3mxr.core.ui.theme.ChemShapes
import com.example.ch3mxr.feature.main.model.DemoCatalog

private data class SearchDemoItem(val title: String, val subtitle: String)

/** Búsqueda local, solo mock: filtra datos de prueba sin conexión a servidor. */
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchContext: String,
    onBack: () -> Unit,
    onResultClick: (String) -> Unit
) {
    var query by rememberSaveable(searchContext) { mutableStateOf("") }
    val entries = remember(searchContext) {
        val courses = DemoCatalog.explore + DemoCatalog.maths
        val sample = courses.map { SearchDemoItem(it.title, "${it.area} · ${it.subtitle}") }
        val communities = listOf(
            SearchDemoItem("Curiosos por la ciencia", "Comunidad · 342 miembros"),
            SearchDemoItem("Programamos juntos", "Comunidad · 186 miembros")
        )
        val events = listOf(SearchDemoItem("Repasar estructura atómica", "Agenda · Actividad de ejemplo"))
        when (searchContext) {
            "Mis cursos" -> DemoCatalog.myCourses.map { SearchDemoItem(it.title, it.subtitle) }
            "Comunidad" -> communities
            "Agenda" -> events
            else -> sample + communities + events
        }
    }
    val filtered = remember(query, entries) {
        entries.filter { query.isBlank() || it.title.contains(query.trim(), ignoreCase = true)
            || it.subtitle.contains(query.trim(), ignoreCase = true) }
    }
    val colors = MaterialTheme.colorScheme
    Column(modifier.fillMaxSize().background(colors.background)) {
        Row(modifier = Modifier.fillMaxWidth().statusBarsPadding()
            .height(ChemDimens.DetailTopBarHeight).padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically) {
            ChemIconAction(Icons.Outlined.ArrowBack, "Volver", onBack)
            OutlinedTextField(
                value = query, onValueChange = { query = it },
                modifier = Modifier.weight(1f).height(ChemDimens.SearchInputHeight),
                placeholder = { Text("Buscar en $searchContext", style = MaterialTheme.typography.bodySmall) },
                singleLine = true,
                leadingIcon = { Icon(Icons.Outlined.Search, null) },
                trailingIcon = { if (query.isNotBlank()) ChemIconAction(Icons.Outlined.Close, "Limpiar", { query = "" }) },
                shape = ChemShapes.Component,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                textStyle = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.width(9.dp))
        }
        HorizontalDivider(color = colors.outline.copy(alpha = .3f))
        Text(if (query.isBlank()) "Sugerencias" else "Resultados (${filtered.size})",
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 14.dp),
            style = MaterialTheme.typography.titleSmall, color = colors.onSurfaceVariant)
        LazyColumn(contentPadding = PaddingValues(horizontal = 15.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtered, key = { "${it.title}:${it.subtitle}" }) { item ->
                ChemMenuGroup {
                    ChemMenuRow(Icons.Outlined.Search, item.title, item.subtitle,
                        onClick = { onResultClick(item.title) }, showDivider = false)
                }
            }
            if (filtered.isEmpty()) item {
                Text("No encontramos coincidencias en los datos de demostración.",
                    color = colors.onSurfaceVariant)
            }
        }
    }
}
