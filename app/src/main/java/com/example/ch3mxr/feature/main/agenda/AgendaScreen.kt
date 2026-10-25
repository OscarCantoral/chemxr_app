package com.example.ch3mxr.feature.main.agenda

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.ch3mxr.core.ui.theme.ChemShapes
import com.example.ch3mxr.core.ui.components.ChemButton
import com.example.ch3mxr.core.ui.components.ChemButtonStyle
import com.example.ch3mxr.feature.experience.*
import com.example.ch3mxr.feature.main.components.*
import com.example.ch3mxr.feature.profile.model.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.DayOfWeek
import java.util.Locale
import org.json.JSONArray
import org.json.JSONObject

private data class AgendaEntry(
    val ownerId:String,val title:String,val type:String,
    val start:LocalDateTime,val end:LocalDateTime,val allDay:Boolean=false,
    val course:String=""
)

private fun demoAgenda():List<AgendaEntry> {
    val day=LocalDate.now()
    fun at(d:LocalDate,h:Int,m:Int=0)=d.atTime(h,m)
    return listOf(
        AgendaEntry("personal","Repasar estructura atómica","Plan personal",at(day,17),at(day,18)),
        AgendaEntry("utp","Redes y Comunicación de Datos II","Clase",at(day,20,15),at(day,21,45),course="Redes II"),
        AgendaEntry("utp","Mecánica Clásica · sesión 11","Clase",at(day.plusDays(2),20,15),at(day.plusDays(2),21,45),course="Mecánica Clásica"),
        AgendaEntry("utp","Entrega de investigación","Entrega",at(day.plusDays(1),19),at(day.plusDays(1),20),course="Formación para la Investigación"),
        AgendaEntry("utp","Examen de redes · semana 12","Examen",at(day.plusDays(3),18),at(day.plusDays(3),19,30),course="Redes II"),
        AgendaEntry("utp","Preparación de exposición grupal","Actividad",at(day.minusDays(1),0),at(day.plusDays(2),23,59),true,course="Investigación"),
        AgendaEntry("senati","Programación de aplicaciones","Clase",at(day,16,30),at(day,18),course="Programación"),
        AgendaEntry("senati","Vencimiento práctica de redes","Entrega",at(day.plusDays(2),19),at(day.plusDays(2),20),course="Redes"),
        AgendaEntry("senati","Presentación de proyecto","Presentación",at(day.plusDays(4),0),at(day.plusDays(4),23,59),true)
    )
}

private fun readAgenda(context:Context):List<AgendaEntry> = runCatching {
    val raw=context.getSharedPreferences("chemxr_agenda_personal",Context.MODE_PRIVATE).getString("entries","[]")?:"[]"
    val arr=JSONArray(raw)
    (0 until arr.length()).map { i ->
        val x=arr.getJSONObject(i)
        AgendaEntry(x.getString("owner"),x.getString("title"),x.getString("type"),
            LocalDateTime.parse(x.getString("start")),LocalDateTime.parse(x.getString("end")),x.optBoolean("allDay"))
    }
}.getOrDefault(emptyList())

private fun saveAgenda(context:Context, entries:List<AgendaEntry>) {
    val array=JSONArray()
    entries.forEach { entry -> array.put(JSONObject().apply {
        put("owner",entry.ownerId);put("title",entry.title);put("type",entry.type)
        put("start",entry.start.toString());put("end",entry.end.toString());put("allDay",entry.allDay)
    }) }
    context.getSharedPreferences("chemxr_agenda_personal",Context.MODE_PRIVATE).edit().putString("entries",array.toString()).apply()
}

/** Calendario del estudiante: fuentes externas únicamente en Mi espacio. */
@Composable
fun AgendaScreen(
    modifier:Modifier=Modifier,space:StudySpace=DemoStudySpaces.available.first(),
    includeOther:Set<String> = emptySet(),onIncludeOtherChange:(String,Boolean)->Unit={_,_->}
) {
    val context=LocalContext.current
    val today=remember {LocalDate.now()}
    var date by rememberSaveable(space.id) {mutableStateOf(today.toString())}
    val selected=runCatching {LocalDate.parse(date)}.getOrDefault(today)
    val weekStart=selected.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    var filter by rememberSaveable {mutableStateOf("Todos")}
    var showSources by rememberSaveable {mutableStateOf(false)}
    var create by rememberSaveable {mutableStateOf(false)}
    var entries by remember {mutableStateOf(readAgenda(context))}
    val allowed=if(space.academic) setOf(space.id) else setOf("personal")+includeOther
    val all=(remember {demoAgenda()}+entries).filter {it.ownerId in allowed}
    fun overlaps(item:AgendaEntry,day:LocalDate):Boolean =
        !day.isBefore(item.start.toLocalDate()) && !day.isAfter(item.end.toLocalDate())
    val visible=all.filter {overlaps(it,selected) && (filter=="Todos" || it.type==filter)}
    val spanning=visible.filter {it.allDay || it.start.toLocalDate()!=it.end.toLocalDate()}
    val timed=visible.filterNot {it in spanning}.sortedBy {it.start}
    val es=Locale("es","PE")
    MainLazyPage(modifier) {
        item {Eyebrow("${space.name} · Tu calendario académico")}
        item {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween) {
                Heading("Tu agenda.",29)
                IconButton(onClick={create=true}) {Icon(Icons.Outlined.AddCircleOutline,"Agregar actividad",tint=MaterialTheme.colorScheme.primary)}
            }
        }
        item {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(selected.format(DateTimeFormatter.ofPattern("MMMM 'de' yyyy",es)).replaceFirstChar {it.uppercase()},fontWeight=FontWeight.Bold)
                    Text("Semana ${weekStart.format(DateTimeFormatter.ofPattern("dd/MM"))} – ${weekStart.plusDays(6).format(DateTimeFormatter.ofPattern("dd/MM"))}",style=MaterialTheme.typography.labelSmall)
                }
                TextButton(onClick={date=today.toString()}) {Text("Hoy")}
                IconButton(onClick={date=selected.minusWeeks(1).toString()}) {Icon(Icons.Outlined.ChevronLeft,"Semana anterior")}
                IconButton(onClick={date=selected.plusWeeks(1).toString()}) {Icon(Icons.Outlined.ChevronRight,"Semana siguiente")}
            }
        }
        item {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(4.dp)) {
                (0L..6L).forEach {offset ->
                    val d=weekStart.plusDays(offset)
                    val chosen=d==selected
                    Surface(onClick={date=d.toString()},shape=ChemShapes.Component,
                        color=if(chosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        border=BorderStroke(1.dp,MaterialTheme.colorScheme.outline.copy(alpha=.3f)),
                        modifier=Modifier.weight(1f)) {
                        Column(Modifier.padding(vertical=9.dp),horizontalAlignment=Alignment.CenterHorizontally) {
                            Text(d.format(DateTimeFormatter.ofPattern("EE",es)).take(2),style=MaterialTheme.typography.labelSmall,
                                color=if(chosen) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${d.dayOfMonth}",fontWeight=FontWeight.Bold,
                                color=if(chosen) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                            if(all.any {overlaps(it,d)}) Text("•",color=if(chosen)MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
        item {
            Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(5.dp)) {
                listOf("Todos","Clase","Entrega","Examen","Tarea académica","Presentación","Exposición","Actividad","Plan personal").forEach {kind ->
                    FilterChip(selected=kind==filter,onClick={filter=kind},label={Text(kind)},shape=ChemShapes.Component)
                }
            }
        }
        if(!space.academic) {
            item {DemoNavRow("Calendarios visibles","Incluir fechas de UTP o Senati únicamente en Mi espacio",Icons.Outlined.FilterList) {showSources=!showSources}}
            if(showSources) item {
                GroupPanel("Fuentes de calendario", "Las instituciones mantienen sus agendas separadas") {
                    DemoStudySpaces.available.forEach {owner ->
                        Row(verticalAlignment=Alignment.CenterVertically) {
                            Checkbox(checked=owner.id=="personal" || owner.id in includeOther,
                                enabled=owner.id!="personal",onCheckedChange={if(owner.id!="personal")onIncludeOtherChange(owner.id,it)})
                            SpaceMark(owner,22.dp);Spacer(Modifier.width(8.dp));Text(owner.name)
                        }
                    }
                }
            }
        }
        item {SectionHeading("${selected.format(DateTimeFormatter.ofPattern("EEEE d",es)).replaceFirstChar {it.uppercase()}} · Actividades")}
        if(spanning.isNotEmpty()) {
            item {Eyebrow("EVENTOS DE DÍA COMPLETO O VARIOS DÍAS")}
            items(spanning.size) {i ->AgendaCard(spanning[i],false)}
        }
        item {Eyebrow("HORARIO Y VENCIMIENTOS")}
        if(timed.isEmpty()) item {DemoPanel {Text("No hay clases ni vencimientos con estos filtros.")}}
        else items(timed.size) {i ->AgendaCard(timed[i],true)}
        item {
            Text("Las entregas se muestran durante su última hora previa al cierre; los eventos de varios días aparecen arriba.",
                style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {OutlinedButton(onClick={create=true},modifier=Modifier.fillMaxWidth(),shape=ChemShapes.Component) {
            Icon(Icons.Outlined.EditCalendar,null);Spacer(Modifier.width(8.dp));Text("Registrar actividad del sílabo")
        }}
    }
    if(create) AddAgendaDialog(space.id,selected,onDismiss={create=false},onSave={entry ->
        entries=entries+entry;saveAgenda(context,entries);date=entry.start.toLocalDate().toString();create=false
    })
}

@Composable
private fun AgendaCard(entry:AgendaEntry,clocked:Boolean) {
    val owner=DemoStudySpaces.available.firstOrNull {it.id==entry.ownerId}?:DemoStudySpaces.available.first()
    val time=if(clocked) "${entry.start.toLocalTime().toString().take(5)} – ${entry.end.toLocalTime().toString().take(5)}"
        else "${entry.start.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM"))} – ${entry.end.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM"))}"
    Surface(color=MaterialTheme.colorScheme.surface,shape=ChemShapes.Component,
        border=BorderStroke(1.dp,owner.primary.copy(alpha=.5f)),modifier=Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(12.dp),horizontalArrangement=Arrangement.spacedBy(10.dp)) {
            Box(Modifier.width(4.dp).height(62.dp).background(owner.primary))
            Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(3.dp)) {
                Text(entry.title,fontWeight=FontWeight.Bold,maxLines=2,overflow=TextOverflow.Ellipsis)
                Text("$time · ${entry.type}",style=MaterialTheme.typography.bodySmall)
                Text(listOf(owner.name,entry.course).filter {it.isNotBlank()}.joinToString(" · "),
                    style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun AddAgendaDialog(ownerId:String,date:LocalDate,onDismiss:()->Unit,onSave:(AgendaEntry)->Unit) {
    var title by remember {mutableStateOf("")}
    var kind by remember {mutableStateOf("Tarea académica")}
    var startText by remember {mutableStateOf(date.toString())}
    var endText by remember {mutableStateOf(date.toString())}
    var timeText by remember {mutableStateOf("20:00")}
    var spanning by remember {mutableStateOf(false)}
    var invalid by remember {mutableStateOf(false)}
    AlertDialog(onDismissRequest=onDismiss,shape=ChemShapes.Component,
        title={Text("Nueva actividad académica")},text={
            Column(verticalArrangement=Arrangement.spacedBy(7.dp)) {
                OutlinedTextField(title,{title=it},label={Text("Nombre de la actividad")},singleLine=true)
                Text("Tipo de actividad",style=MaterialTheme.typography.labelSmall)
                Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(5.dp)) {
                    listOf("Tarea académica","Examen","Presentación","Exposición","Plan personal").forEach {option ->
                        FilterChip(selected=kind==option,onClick={kind=option},label={Text(option)},shape=ChemShapes.Component)
                    }
                }
                OutlinedTextField(startText,{startText=it},label={Text("Fecha inicio (AAAA-MM-DD)")},singleLine=true)
                Row(verticalAlignment=Alignment.CenterVertically) {
                    Checkbox(checked=spanning,onCheckedChange={spanning=it})
                    Text("Actividad de varios días")
                }
                if(spanning) OutlinedTextField(endText,{endText=it},label={Text("Fecha fin (AAAA-MM-DD)")},singleLine=true)
                else OutlinedTextField(timeText,{timeText=it},label={Text("Hora de cierre o inicio (HH:mm)")},singleLine=true)
                if(invalid) Text("Revisa las fechas, hora y nombre.",color=MaterialTheme.colorScheme.error)
            }
        },confirmButton={
            TextButton(onClick={
                val entry=runCatching {
                    val first=LocalDate.parse(startText)
                    val last=if(spanning)LocalDate.parse(endText) else first
                    require(!last.isBefore(first) && title.isNotBlank())
                    if(spanning) AgendaEntry(ownerId,title,kind,first.atStartOfDay(),last.atTime(23,59),true)
                    else {
                        val end=first.atTime(LocalTime.parse(timeText))
                        AgendaEntry(ownerId,title,kind,if(kind=="Tarea académica")end.minusHours(1) else end,
                            if(kind=="Tarea académica")end else end.plusHours(1))
                    }
                }.getOrNull()
                if(entry==null)invalid=true else onSave(entry)
            }) {Text("Guardar")}
        },dismissButton={TextButton(onClick=onDismiss){Text("Cancelar")}})
}
