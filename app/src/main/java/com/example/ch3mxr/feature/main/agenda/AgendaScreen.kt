package com.example.ch3mxr.feature.main.agenda

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.components.ChemSelectablePanel
import com.example.ch3mxr.core.ui.components.ChemPanel
import com.example.ch3mxr.feature.experience.*
import com.example.ch3mxr.feature.main.components.*
import com.example.ch3mxr.feature.profile.model.*

/** Únicamente Mi espacio puede incorporar calendarios institucionales externos. */
@Composable
fun AgendaScreen(
    modifier: Modifier = Modifier,
    space: StudySpace = DemoStudySpaces.available.first(),
    includeOther: Set<String> = emptySet(),
    onIncludeOtherChange: (String, Boolean) -> Unit = { _, _ -> }
) {
    var day by rememberSaveable { mutableIntStateOf(24) }
    var category by rememberSaveable { mutableStateOf("Todo") }
    var showSources by rememberSaveable { mutableStateOf(false) }
    val visibleSpaces = if(space.academic) setOf(space.id) else setOf("personal") + includeOther
    val events = ChemDemoData.events.filter { it.spaceId in visibleSpaces && it.day == day && (category=="Todo" || it.kind==category) }
    MainLazyPage(modifier) {
        item { SpaceContextHeader(space,"A tu ritmo · Octubre de 2026") }
        item {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween) {
                Heading("Tu agenda.",28)
                Icon(Icons.Outlined.CalendarMonth,null,tint=MaterialTheme.colorScheme.primary)
            }
        }
        item {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(5.dp)) {
                listOf("Lu","Ma","Mi","Ju","Vi","Sá","Do").forEachIndexed { index,label ->
                    val d=19+index
                    ChemSelectablePanel(selected=day==d,modifier=Modifier.weight(1f).height(70.dp),onClick={day=d}) {
                        Column(horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center) {
                            Text(label,style=MaterialTheme.typography.labelSmall)
                            Text(d.toString(),fontWeight=FontWeight.Bold)
                        }
                    }
                }
            }
        }
        item {
            Row(horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                listOf("Todo","Clase","Entrega").forEach { choice ->
                    DemoFilterButton(choice, category==choice,Modifier.weight(1f)) {category=choice}
                }
            }
        }
        if(!space.academic) {
            item {
                DemoNavRow("Fuentes de calendario", "Selecciona qué instituciones aparecen solo aquí",Icons.Outlined.FilterList) {
                    showSources=!showSources
                }
            }
            if(showSources) item {
                ChemPanel {
                    Text("Mostrar eventos de:",fontWeight=FontWeight.Bold)
                    DemoStudySpaces.available.forEach { option ->
                        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                            Checkbox(checked=option.id=="personal" || option.id in includeOther,
                                onCheckedChange={checked->if(option.id!="personal")onIncludeOtherChange(option.id,checked)},
                                enabled=option.id!="personal")
                            SpaceMark(option,24.dp)
                            Spacer(Modifier.width(8.dp))
                            Text(option.name)
                        }
                    }
                    Text("La agenda de UTP nunca muestra eventos de Senati, ni viceversa.",style=MaterialTheme.typography.labelSmall)
                }
            }
        }
        item { SectionHeading("Tu día, paso a paso") }
        if(events.isEmpty()) item {
            ChemPanel {Text("No hay actividades para este día y filtros.")}
        } else {
            items(events.size) { index ->
                val event=events[index]
                val owner=DemoStudySpaces.available.first {it.id==event.spaceId}
                Surface(
                    color=MaterialTheme.colorScheme.surface,shape=com.example.ch3mxr.core.ui.theme.ChemShapes.Component,
                    border=BorderStroke(1.dp,owner.primary.copy(alpha=.55f)),modifier=Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.padding(13.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(11.dp)) {
                        SpaceMark(owner,32.dp)
                        Column(Modifier.weight(1f)) {
                            Text(event.title,fontWeight=FontWeight.Bold)
                            Text("${event.time} · ${event.kind} · ${owner.name}",style=MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
        item {
            GroupPanel("Mi objetivo semanal", "4 actividades por semana · DEMO") {
                LinearProgressIndicator(progress={.25f},modifier=Modifier.fillMaxWidth())
            }
        }
    }
}
