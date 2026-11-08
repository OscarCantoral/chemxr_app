package com.example.ch3mxr.feature.selection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.components.ChemButton
import com.example.ch3mxr.core.ui.components.ChemButtonStyle
import com.example.ch3mxr.core.ui.components.ChemPanel
import com.example.ch3mxr.core.ui.theme.ChemShapes
import com.example.ch3mxr.feature.main.components.Eyebrow
import com.example.ch3mxr.feature.main.components.Heading
import com.example.ch3mxr.feature.main.components.IconTile
import com.example.ch3mxr.feature.main.components.MainLazyPage
import com.example.ch3mxr.feature.main.components.SectionHeading

/** Vista de compra personal. El shell conserva los IDs al navegar o cambiar de espacio. */
@Composable
fun CourseSelectionScreen(
    selectedCourses: List<SelectionCourse>,
    availableCourses: List<SelectionCourse>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit,
    onCheckout: () -> Unit,
    onExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val subtotal = selectedCourses.sumOf { it.regularPriceCents }
    val total = selectedCourses.sumOf { it.priceCents }
    val savings = subtotal - total
    var showCheckout by rememberSaveable { mutableStateOf(false) }

    Column(modifier.navigationBarsPadding()) {
        MainLazyPage(Modifier.fillMaxWidth().weight(1f)) {
            item(key = "intro") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Eyebrow("Mi espacio · Próximos aprendizajes")
                    Heading("Aprender empieza aquí.", 26)
                    Text("Revisa los cursos que has elegido y continúa con tu compra.",
                        color = colors.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                }
            }
            if (selectedCourses.isEmpty()) {
                item(key = "empty") {
                    ChemPanel {
                        Icon(Icons.Outlined.ShoppingCart, null, Modifier.size(40.dp), tint = colors.primary)
                        Spacer(Modifier.height(16.dp))
                        Text("Tu selección está vacía", style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text("Añade un curso de las sugerencias o sigue explorando nuevas áreas.",
                            color = colors.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(18.dp))
                        ChemButton("Explorar cursos", onExplore, Modifier.fillMaxWidth())
                    }
                }
            } else {
                item(key = "selected_heading") {
                    SectionHeading("${selectedCourses.size} ${if (selectedCourses.size == 1) "curso seleccionado" else "cursos seleccionados"}")
                }
                items(selectedCourses, key = { "selected_${it.id}" }) { product ->
                    SelectionCourseCard(product = product, selected = true, onAction = { onRemove(product.id) })
                }
                item(key = "summary") {
                    ChemPanel {
                        Text("Resumen de tu selección", style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(14.dp))
                        PriceRow("Precio regular", formatCoursePrice(subtotal))
                        if (savings > 0) {
                            Spacer(Modifier.height(8.dp))
                            PriceRow("Descuento en cursos", "− ${formatCoursePrice(savings)}", accent = true)
                        }
                        HorizontalDivider(Modifier.padding(vertical = 14.dp), color = colors.outline.copy(alpha = .3f))
                        PriceRow("Total", formatCoursePrice(total), bold = true)
                        Spacer(Modifier.height(10.dp))
                        Text("Pago único · Precios en soles", style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant)
                    }
                }
            }
            if (availableCourses.isNotEmpty()) {
                item(key = "suggestions_heading") { SectionHeading("Sigue descubriendo") }
                items(availableCourses, key = { "available_${it.id}" }) { product ->
                    SelectionCourseCard(product = product, selected = false, onAction = { onAdd(product.id) })
                }
            }
            if (selectedCourses.isNotEmpty()) {
                item(key = "explore") {
                    ChemButton("Seguir explorando", onExplore, Modifier.fillMaxWidth(), style = ChemButtonStyle.OUTLINED)
                }
            }
        }
        if (selectedCourses.isNotEmpty()) {
            Surface(color = colors.surface) {
                Column {
                    HorizontalDivider(color = colors.outline.copy(alpha = .3f))
                    Column(Modifier.padding(horizontal = 15.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        PriceRow("Total (${selectedCourses.size} ${if (selectedCourses.size == 1) "curso" else "cursos"})",
                            formatCoursePrice(total), bold = true)
                        ChemButton("Continuar con la compra", { showCheckout = true }, Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }

    if (showCheckout && selectedCourses.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { showCheckout = false },
            shape = ChemShapes.Component,
            containerColor = colors.surface,
            title = { Text("Revisar compra") },
            text = {
                Column(Modifier.heightIn(max = 320.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    selectedCourses.forEach { product ->
                        PriceRow(product.course.title, formatCoursePrice(product.priceCents))
                    }
                    HorizontalDivider(color = colors.outline.copy(alpha = .3f))
                    PriceRow("Total", formatCoursePrice(total), bold = true)
                    Text("Compra de demostración. No se realizará ningún cobro; los cursos se añadirán a Mis cursos durante esta sesión.",
                        color = colors.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                ChemButton("Simular compra", {
                    showCheckout = false
                    onCheckout()
                })
            },
            dismissButton = {
                TextButton(onClick = { showCheckout = false }, shape = ChemShapes.Component) { Text("Volver") }
            }
        )
    }
}

@Composable
private fun SelectionCourseCard(product: SelectionCourse, selected: Boolean, onAction: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val course = product.course
    ChemPanel {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconTile(course.icon, course.tileColor, course.iconColor, 56)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(course.area, style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant)
                Text(course.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(product.instructor, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
                Text("${product.duration} · ${product.lessons} lecciones · A tu ritmo",
                    style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(formatCoursePrice(product.priceCents), style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
                if (product.regularPriceCents > product.priceCents) {
                    Text(formatCoursePrice(product.regularPriceCents),
                        style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.LineThrough),
                        color = colors.onSurfaceVariant)
                }
            }
            ChemButton(
                text = if (selected) "Quitar" else "Añadir",
                onClick = onAction,
                modifier = Modifier.width(110.dp),
                style = if (selected) ChemButtonStyle.OUTLINED else ChemButtonStyle.FILLED,
                leadingIcon = if (selected) Icons.Outlined.DeleteOutline else Icons.Outlined.Add
            )
        }
    }
}

@Composable
private fun PriceRow(label: String, price: String, bold: Boolean = false, accent: Boolean = false) {
    val colors = MaterialTheme.colorScheme
    val weight = if (bold) FontWeight.Bold else FontWeight.Normal
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium,
            fontWeight = weight, color = colors.onSurface)
        Text(price, style = MaterialTheme.typography.bodyMedium, fontWeight = weight,
            color = if (accent) colors.primary else colors.onSurface)
    }
}
