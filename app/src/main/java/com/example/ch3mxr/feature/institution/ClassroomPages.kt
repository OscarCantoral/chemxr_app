package com.example.ch3mxr.feature.institution

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.components.*
import com.example.ch3mxr.core.ui.theme.ChemShapes
import com.example.ch3mxr.feature.experience.*
import com.example.ch3mxr.feature.learning.*
import com.example.ch3mxr.feature.library.catalog.LocalResourceImage
import com.example.ch3mxr.feature.main.components.*
import com.example.ch3mxr.feature.profile.model.StudySpace
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun InstitutionalCoursePage(courseId:String,space:StudySpace,modifier:Modifier=Modifier,onOpen:(String)->Unit) {
    val store=LocalLearningStore.current
    val course=ClassroomCatalog.find(courseId)?.takeIf {it.spaceId==space.id}
    if(course==null) {MainPage(modifier) {Heading("Curso no disponible.")};return}
    MainPage(modifier.navigationBarsPadding()) {
        SpaceContextHeader(space,"Curso institucional")
        Heading(course.title)
        DemoBadge(course.sectionName)
        GroupPanel("Docente", "Asignación institucional de demostración") {Text(course.teacher)}
        ChemButton(if(store.isSaved("institutionCourse:${course.id}")) "Quitar de Mi estante" else "Guardar en Mi estante",{
            store.toggleShortcut(SavedShortcut("institutionCourse:${course.id}",space.id,course.title,"Curso","institutionCourse/${course.id}"))
        },Modifier.fillMaxWidth(),style=ChemButtonStyle.OUTLINED)
        DemoNavRow("Eventos y apuntes del salón","Grabaciones, referencias por minuto y fotos de pizarra",Icons.Outlined.Groups) {onOpen("classroom/${course.id}")}
        listOf(
            Triple("Contenido y recursos","Módulos y prácticas","modules"),
            Triple("Sílabo","Plan del curso","syllabus"),
            Triple("Exámenes y tareas","Evaluaciones y rúbricas","assignments"),
            Triple("Biblioteca institucional","Archivos y referencias ordenados","library"),
            Triple("Foro del curso","Preguntas y respuestas con votos","forum"),
            Triple("Mis apuntes privados","Texto e imágenes personales","notes"),
            Triple("Calificaciones","Resultados y retroalimentación","grades"),
            Triple("Mi equipo","Integrantes y conversación","team")
        ).forEach {(title,subtitle,route)->DemoNavRow(title,subtitle) {onOpen(route)}}
    }
}

@Composable
fun ClassroomPage(courseId:String,space:StudySpace,modifier:Modifier=Modifier,onOpen:(String)->Unit) {
    val store=LocalLearningStore.current
    val course=ClassroomCatalog.find(courseId)?.takeIf {it.spaceId==space.id}
    if(course==null) {MainPage(modifier) {Heading("Salón no disponible.")};return}
    val role=store.role(course.id)
    val canPublish=LearningRules.canPublishClass(role)
    val context=LocalContext.current
    var eventEditor by rememberSaveable(course.id) {mutableStateOf(false)}
    var draft by rememberSaveable(course.id) {mutableStateOf("")}
    var timestamp by rememberSaveable(course.id) {mutableStateOf("12:30")}
    var photo by rememberSaveable(course.id) {mutableStateOf("")}
    var error by rememberSaveable(course.id) {mutableStateOf("")}
    var position by rememberSaveable(course.id) {mutableIntStateOf(0)}
    val recording="${course.id}_session10"
    val notes=store.classNotes.filter {it.courseId==course.id && it.sectionId==course.sectionId && it.recordingId==recording}
    val events=store.classEvents.filter {it.courseId==course.id && it.sectionId==course.sectionId}.sortedBy {it.start}
    val picker=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {uri:Uri?->
        if(uri!=null) {
            runCatching {context.contentResolver.takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION)}
            photo=uri.toString()
        }
    }
    MainPage(modifier.navigationBarsPadding()) {
        SpaceContextHeader(space,"Salón del curso")
        Heading(course.title)
        DemoBadge(course.sectionName)
        GroupPanel("Rol de demostración","Este selector solo permite probar permisos en el prototipo") {
            Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                AcademicRole.entries.forEach {value->FilterChip(selected=role==value,onClick={store.setDemoRole(course.id,value)},label={Text(value.label)},shape=ChemShapes.Component)}
            }
        }
        GroupPanel("Eventos del salón","Visibles para todos los estudiantes de este curso y salón") {
            if(events.isEmpty()) Text("Todavía no hay eventos compartidos.")
            events.forEach {item->
                Text(item.title,style=MaterialTheme.typography.titleSmall)
                Text("${item.start.replace('T',' ')} · ${item.type}",style=MaterialTheme.typography.bodySmall)
                Text("Publicado por ${item.authorRole.label}",style=MaterialTheme.typography.labelSmall)
                HorizontalDivider(Modifier.padding(vertical=8.dp))
            }
            if(canPublish) ChemButton("Crear evento para el salón",{eventEditor=true},Modifier.fillMaxWidth())
            else Text("El delegado y el docente pueden publicar eventos del salón.",style=MaterialTheme.typography.bodySmall)
        }
        GroupPanel("Grabación · Sesión 10","Duración de muestra: 55:00") {
            Icon(Icons.Outlined.SmartDisplay,null,Modifier.fillMaxWidth().height(110.dp),tint=MaterialTheme.colorScheme.primary)
            Text("Referencia seleccionada: ${LearningRules.formatTimestamp(position)}")
            Text("Grabación de ejemplo · sin archivo de vídeo.",style=MaterialTheme.typography.labelSmall)
        }
        if(canPublish) GroupPanel("Crear apunte compartido","El minuto y la foto quedan vinculados a esta grabación") {
            OutlinedTextField(timestamp,{timestamp=it},label={Text("Minuto de la grabación · MM:SS")},singleLine=true,modifier=Modifier.fillMaxWidth())
            OutlinedTextField(draft,{draft=it},label={Text("Apunte o explicación de la pizarra")},minLines=3,modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            ChemButton(if(photo.isBlank()) "Adjuntar foto de pizarra" else "Cambiar foto",{picker.launch(arrayOf("image/*"))},Modifier.fillMaxWidth(),style=ChemButtonStyle.OUTLINED)
            if(photo.isNotBlank()) {
                LocalResourceImage(photo,"Foto de pizarra seleccionada")
                TextButton(onClick={photo=""}) {Text("Quitar foto")}
            }
            if(error.isNotBlank()) Text(error,color=MaterialTheme.colorScheme.error)
            ChemButton("Publicar apunte para el salón",{
                val seconds=LearningRules.parseTimestamp(timestamp)
                if(seconds!=null && store.addClassNote(course,recording,seconds,draft,photo)) {
                    draft="";photo="";error="";position=seconds
                } else error="Usa un minuto entre 00:00 y 55:00 y añade texto o una foto."
            },Modifier.fillMaxWidth())
        }
        SectionHeading("Apuntes de la grabación")
        if(notes.isEmpty()) Text("Todavía no hay apuntes compartidos para esta grabación.")
        notes.forEach {note->
            GroupPanel("${LearningRules.formatTimestamp(note.seconds)} · ${note.authorRole.label}",course.sectionName) {
                if(note.text.isNotBlank()) Text(note.text)
                if(note.photoUri.isNotBlank()) LocalResourceImage(note.photoUri,"Foto de la pizarra")
                ChemButton("Ir a ${LearningRules.formatTimestamp(note.seconds)}",{position=note.seconds},Modifier.fillMaxWidth(),style=ChemButtonStyle.OUTLINED)
            }
        }
        DemoNavRow("Biblioteca institucional","Las fotos compartidas también se registran como recursos",Icons.Outlined.LocalLibrary) {onOpen("library")}
    }
    if(eventEditor && canPublish) ClassEventDialog(course,{eventEditor=false})
}

@Composable
private fun ClassEventDialog(course:ClassroomCourse,onDismiss:()->Unit) {
    val store=LocalLearningStore.current
    var title by rememberSaveable {mutableStateOf("")}
    var date by rememberSaveable {mutableStateOf(LocalDate.now().toString())}
    var from by rememberSaveable {mutableStateOf("18:00")}
    var until by rememberSaveable {mutableStateOf("19:00")}
    var type by rememberSaveable {mutableStateOf("Actividad")}
    var error by rememberSaveable {mutableStateOf(false)}
    AlertDialog(onDismissRequest=onDismiss,shape=ChemShapes.Component,title={Text("Evento del salón")},
        text={Column(Modifier.heightIn(max=420.dp).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(8.dp)) {
            Text("${course.title} · ${course.sectionName}")
            Text("Se mostrará a todos los estudiantes de este salón.")
            OutlinedTextField(title,{title=it},label={Text("Título")},modifier=Modifier.fillMaxWidth())
            OutlinedTextField(date,{date=it},label={Text("Fecha · AAAA-MM-DD")},singleLine=true,modifier=Modifier.fillMaxWidth())
            OutlinedTextField(from,{from=it},label={Text("Inicio · HH:MM")},singleLine=true,modifier=Modifier.fillMaxWidth())
            OutlinedTextField(until,{until=it},label={Text("Fin · HH:MM")},singleLine=true,modifier=Modifier.fillMaxWidth())
            Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(5.dp)) {
                listOf("Actividad","Clase","Entrega","Examen").forEach {value->FilterChip(selected=type==value,onClick={type=value},label={Text(value)},shape=ChemShapes.Component)}
            }
            if(error) Text("Revisa el nombre, la fecha y las horas. El final debe ser posterior al inicio.",color=MaterialTheme.colorScheme.error)
        }},confirmButton={TextButton(onClick={
            val saved=runCatching {store.addClassEvent(course,title,type,LocalDateTime.parse("${date}T$from"),LocalDateTime.parse("${date}T$until"))}.getOrDefault(false)
            if(saved) onDismiss() else error=true
        }) {Text("Publicar evento")}},dismissButton={TextButton(onClick=onDismiss) {Text("Cancelar")}})
}
