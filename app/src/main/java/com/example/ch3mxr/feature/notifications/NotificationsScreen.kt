package com.example.ch3mxr.feature.notifications

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.theme.*
import com.example.ch3mxr.feature.experience.*
import com.example.ch3mxr.feature.main.components.*
import com.example.ch3mxr.feature.profile.model.*

@Composable
fun NotificationsTopBar(onBackClick: () -> Unit) {
    Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().height(ChemDimens.TopBarHeight).padding(horizontal=10.dp),verticalAlignment=Alignment.CenterVertically) {
            IconButton(onClick=onBackClick) { Icon(Icons.Outlined.ArrowBack,"Volver") }
            Text("Notificaciones",fontWeight=FontWeight.Bold,style=MaterialTheme.typography.titleLarge)
        }
        HorizontalDivider(color=MaterialTheme.colorScheme.outline.copy(alpha=.3f))
    }
}

@Composable
fun NotificationsScreen(
    modifier:Modifier=Modifier,
    space:StudySpace=DemoStudySpaces.available.first(),
    includeOther:Set<String> = emptySet(),
    onIncludeOtherChange:(String,Boolean)->Unit={_,_->},
    onOpenSettings:()->Unit={}
) {
    var category by rememberSaveable {mutableStateOf("Todas")}
    var showSources by rememberSaveable {mutableStateOf(false)}
    var kind by rememberSaveable {mutableStateOf("Todos los tipos")}
    var read by rememberSaveable {mutableStateOf(false)}
    val ids=if(space.academic) setOf(space.id) else setOf("personal")+includeOther
    val notices=ChemDemoData.notices.filter{
        it.spaceId in ids && (category=="Todas" || !read) && (kind=="Todos los tipos" || it.type==kind)
    }
    MainPage(modifier) {
        SpaceContextHeader(space,"Mensajes, cursos y avisos")
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
            Heading("Al día.",30)
            TextButton(onClick={read=true}) {Text("Marcar leídas")}
        }
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            DemoFilterButton("Todas",category=="Todas",Modifier.weight(1f)){category="Todas"}
            DemoFilterButton("Sin leer",category=="Sin leer",Modifier.weight(1f)){category="Sin leer"}
        }
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement=Arrangement.spacedBy(7.dp)) {
            listOf("Todos los tipos","Institucional","Entrega","Pagos","Curso","Comunidad","Evaluación").forEach { type ->
                FilterChip(selected=kind==type,onClick={kind=type},label={Text(type,style=MaterialTheme.typography.labelSmall)},shape=ChemShapes.Component)
            }
        }
        if(!space.academic) {
            DemoNavRow("Fuentes de notificaciones", "Incluir avisos de UTP y Senati solo en Mi espacio",Icons.Outlined.FilterList){showSources=!showSources}
            if(showSources) GroupPanel("Mostrar notificaciones", "El filtrado no habilita permisos de otros espacios") {
                DemoStudySpaces.available.forEach { other ->
                    Row(verticalAlignment=Alignment.CenterVertically) {
                        Checkbox(checked=other.id=="personal" || other.id in includeOther,
                            onCheckedChange={if(other.id!="personal")onIncludeOtherChange(other.id,it)},enabled=other.id!="personal")
                        Text(other.name)
                    }
                }
            }
        }
        DemoNavRow("Preferencias de avisos", "Push, correo, resumen y recordatorios",Icons.Outlined.Tune){onOpenSettings()}
        if(notices.isEmpty()) Text("No hay notificaciones para este filtro.")
        notices.forEach { notice ->
            val owner=DemoStudySpaces.available.first {it.id==notice.spaceId}
            Surface(shape=ChemShapes.Component,color=MaterialTheme.colorScheme.surface,
                border=BorderStroke(1.dp, owner.primary.copy(alpha=.65f)),modifier=Modifier.fillMaxWidth()) {
                Row(Modifier.padding(13.dp),horizontalArrangement=Arrangement.spacedBy(10.dp),verticalAlignment=Alignment.CenterVertically) {
                    SpaceMark(owner,32.dp)
                    Column(Modifier.weight(1f)) {
                        Text(notice.title,fontWeight=FontWeight.Bold)
                        Text(notice.description,style=MaterialTheme.typography.bodySmall)
                        Text("${owner.name} · ${notice.type}",style=MaterialTheme.typography.labelSmall,color=owner.primary)
                    }
                    if(!read) DemoBadge("Nueva",owner.primary)
                }
            }
        }
    }
}
