package com.example.ch3mxr.feature.experience

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.theme.ChemShapes
import com.example.ch3mxr.feature.profile.model.StudySpace

private data class CampusShortcut(val label:String, val description:String, val route:String, val icon: ImageVector)

/** Centro de servicios institucionales; el alumno consulta, la gestión se realiza en la plataforma web. */
@Composable
fun CampusHubScreen(space:StudySpace,modifier:Modifier=Modifier,onOpen:(String)->Unit) {
    val shortcuts = listOf(
        CampusShortcut("Matrícula y cursos","Oferta y créditos","enrollment",Icons.Outlined.AppRegistration),
        CampusShortcut("Récord y progreso","Malla y calificaciones","record",Icons.Outlined.BarChart),
        CampusShortcut("Cuenta corriente","Cuotas y recibos","finance",Icons.Outlined.Payments),
        CampusShortcut("Documentos oficiales","Reglamentos y normas","documents",Icons.Outlined.Description),
        CampusShortcut("Trámites y solicitudes","Expedientes y atención","procedures",Icons.Outlined.FactCheck),
        CampusShortcut("Servicios y reservas","Ambientes y equipos","services",Icons.Outlined.MeetingRoom),
        CampusShortcut("Biblioteca","Repositorios y libros","library",Icons.Outlined.LocalLibrary),
        CampusShortcut("Canales y comunicados","Noticias oficiales","channels",Icons.Outlined.Campaign),
        CampusShortcut("Bolsa de trabajo","Ofertas y prácticas","jobs",Icons.Outlined.WorkOutline),
        CampusShortcut("Soporte institucional","Ayuda y tickets","support",Icons.Outlined.SupportAgent)
    )
    LazyVerticalGrid(columns=GridCells.Fixed(2),modifier=modifier,
        contentPadding=PaddingValues(horizontal=14.dp,vertical=20.dp),
        horizontalArrangement=Arrangement.spacedBy(11.dp),verticalArrangement=Arrangement.spacedBy(11.dp)) {
        item(span={androidx.compose.foundation.lazy.grid.GridItemSpan(2)}) {
            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp),
                modifier=Modifier.fillMaxWidth().padding(bottom=12.dp)) {
                SpaceMark(space,56.dp)
                Column {
                    Text(space.name,style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Black)
                    Text("Servicios de tu institución",style=MaterialTheme.typography.bodySmall,
                        color=MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        items(shortcuts) { entry ->
            Surface(modifier=Modifier.fillMaxWidth().height(138.dp).clickable {onOpen(entry.route)},
                shape=ChemShapes.Component,color=MaterialTheme.colorScheme.surface,
                border=BorderStroke(1.dp,MaterialTheme.colorScheme.outline.copy(alpha=.25f))) {
                Column(Modifier.fillMaxSize().padding(10.dp),horizontalAlignment=Alignment.CenterHorizontally,
                    verticalArrangement=Arrangement.Center) {
                    Icon(entry.icon,entry.label,Modifier.size(32.dp),tint=MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(10.dp))
                    Text(entry.label,fontWeight=FontWeight.Bold,style=MaterialTheme.typography.bodySmall,
                        textAlign=TextAlign.Center,maxLines=2)
                    Spacer(Modifier.height(4.dp))
                    Text(entry.description,style=MaterialTheme.typography.labelSmall,
                        color=MaterialTheme.colorScheme.onSurfaceVariant,textAlign=TextAlign.Center,maxLines=2)
                }
            }
        }
    }
}
