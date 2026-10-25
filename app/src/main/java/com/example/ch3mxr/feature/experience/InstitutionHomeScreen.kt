package com.example.ch3mxr.feature.experience

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ch3mxr.core.ui.theme.ChemShapes
import com.example.ch3mxr.feature.main.components.*
import com.example.ch3mxr.feature.profile.model.StudySpace
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private data class CampusSlide(val caption:String,val headline:String,val detail:String,val action:String,val route:String)

/** Portada del estudiante. Los servicios administrativos viven en la pestaña institucional central. */
@Composable
fun InstitutionHomeScreen(space:StudySpace,period:String,modifier:Modifier=Modifier,onOpen:(String)->Unit) {
    val slides=if(space.id=="utp") listOf(
        CampusSlide("UNIVERSIDAD TECNOLÓGICA DEL PERÚ","Tu experiencia también nos inspira","Participa en las actividades y novedades de tu universidad.","Explorar comunicados","channels"),
        CampusSlide("DESARROLLO PROFESIONAL","Prepárate para tu próxima oportunidad","Charlas, habilidades y nuevos aprendizajes para tu carrera.","Ver oportunidades","jobs"),
        CampusSlide("BIBLIOTECA DIGITAL","Investiga con más herramientas","Accede a recursos académicos y lecturas recomendadas.","Abrir biblioteca","library")
    ) else listOf(
        CampusSlide("SENATI · FORMACIÓN TECNOLÓGICA","Aprender haciendo transforma tu futuro","Explora las novedades y oportunidades de tu institución.","Ver novedades","channels"),
        CampusSlide("INNOVACIÓN","Impulsa tus habilidades técnicas","Encuentra recursos para complementar tu formación.","Explorar recursos","library"),
        CampusSlide("OPORTUNIDADES","Tu siguiente reto profesional","Descubre ofertas y prácticas relacionadas con tu carrera.","Ver oportunidades","jobs")
    )
    var index by rememberSaveable(space.id) {mutableIntStateOf(0)}
    val today=remember {LocalDate.now()}
    val dayLabel=remember(today) {today.format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy",Locale("es","PE")))}
    val lesson=if(space.id=="utp") "Redes y Comunicación de Datos II" else "Programación de aplicaciones"
    val teacher=if(space.id=="utp") "Docente del curso de Investigación" else "Docente de Programación"
    MainLazyPage(modifier) {
        item { Heading("Bienvenido a ${space.name}.",27) }
        item { CampusCarousel(space,slides,index,{index=(index+slides.size-1)%slides.size},{index=(index+1)%slides.size},onOpen) }
        item {
            GroupPanel("Tu trayectoria","") {
                SimpleInfo("Carrera",space.program)
                SimpleInfo("Periodo",period)
                SimpleInfo("Modalidad",space.modality)
            }
        }
        item { SectionHeading("Hoy · $dayLabel") }
        item {
            GroupPanel("Tu próxima clase", "Horario académico") {
                Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Outlined.Schedule,null,tint=MaterialTheme.colorScheme.primary)
                    Column(Modifier.weight(1f)) {
                        Text(lesson,fontWeight=FontWeight.Bold)
                        Text(if(space.id=="utp") "20:15 – 21:45 · Presencial" else "16:30 – 18:00 · Taller",
                            style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(10.dp))
                TextButton(onClick={onOpen("course")}) {Text("Ir al curso")}
            }
        }
        item { SectionHeading("Comunicado reciente") }
        item {
            DemoPanel(Modifier.clickable {onOpen("channels")}) {
                Eyebrow("Canal oficial · ${space.name}")
                Text(if(space.id=="utp") "Cronograma académico y fechas importantes" else "Novedades de formación y actividades",
                    fontWeight=FontWeight.Bold)
                Text("Consulta el comunicado completo",style=MaterialTheme.typography.bodySmall,
                    color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item { SectionHeading("Actividad reciente de tu curso") }
        item {
            DemoPanel(Modifier.clickable {onOpen("course")}) {
                Text(lesson,fontWeight=FontWeight.Bold)
                Text("$teacher publicó una nueva tarea para esta semana.",style=MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(4.dp))
                Text("Abrir curso",color=MaterialTheme.colorScheme.primary,style=MaterialTheme.typography.labelMedium)
            }
        }
        item { SectionHeading("Por vencer", "Todas",{onOpen("assignments")}) }
        item {
            DemoPanel(Modifier.clickable {onOpen("assignments")}) {
                Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Outlined.AssignmentLate,null,tint=MaterialTheme.colorScheme.primary)
                    Column {
                        Text("Entrega de avance",fontWeight=FontWeight.Bold)
                        Text("Domingo · 20:00 · Formación para la Investigación",style=MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        item { SectionHeading("En tu comunidad") }
        item {
            DemoPanel(Modifier.clickable {onOpen("communityDetail")}) {
                Eyebrow("${space.name} · Comunidad de estudiantes")
                Text("¿Qué recursos recomiendan para preparar el próximo proyecto?",fontWeight=FontWeight.Bold)
                Text("Última publicación de una comunidad a la que perteneces",style=MaterialTheme.typography.bodySmall,
                    color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item { SectionHeading("Recomendado para ti") }
        item {
            DemoPanel(Modifier.clickable {onOpen("library")}) {
                Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Outlined.LocalLibrary,null,tint=MaterialTheme.colorScheme.primary)
                    Column {
                        Text("Lecturas de ${space.program}",fontWeight=FontWeight.Bold)
                        Text("Un recurso de la biblioteca para complementar tus clases",style=MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        item {
            DemoPanel(Modifier.clickable {onOpen("jobs")}) {
                Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Outlined.WorkOutline,null,tint=MaterialTheme.colorScheme.primary)
                    Column {
                        Text("Oportunidad profesional",fontWeight=FontWeight.Bold)
                        Text("Prácticas y ofertas relacionadas con tu especialidad",style=MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun CampusCarousel(space:StudySpace,slides:List<CampusSlide>,index:Int,onPrev:()->Unit,onNext:()->Unit,onOpen:(String)->Unit) {
    val slide=slides[index]
    Surface(shape=ChemShapes.Component,color=space.primary,modifier=Modifier.fillMaxWidth().height(248.dp)) {
        Box(Modifier.fillMaxSize()) {
            Icon(Icons.Outlined.School,null,Modifier.size(170.dp).align(Alignment.CenterEnd).offset(x=48.dp),
                tint=Color.White.copy(alpha=.10f))
            Column(Modifier.fillMaxSize().padding(17.dp),verticalArrangement=Arrangement.SpaceBetween) {
                Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    Text(space.name.uppercase(),fontSize=13.sp,fontWeight=FontWeight.Black,color=Color.White)
                    Text("· ${slide.caption}",fontSize=10.sp,color=Color.White.copy(alpha=.78f),maxLines=2)
                }
                Column(verticalArrangement=Arrangement.spacedBy(9.dp)) {
                    Text(slide.headline,fontSize=25.sp,lineHeight=28.sp,fontWeight=FontWeight.Black,color=Color.White,maxLines=3,overflow=TextOverflow.Ellipsis)
                    Text(slide.detail,fontSize=12.sp,color=Color.White.copy(alpha=.88f),maxLines=2)
                    TextButton(onClick={onOpen(slide.route)},colors=ButtonDefaults.textButtonColors(contentColor=Color.White)) {
                        Text("${slide.action}  →",fontWeight=FontWeight.Bold)
                    }
                }
                Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween) {
                    Row(horizontalArrangement=Arrangement.spacedBy(5.dp)) {
                        slides.indices.forEach { i -> Box(Modifier.width(if(i==index) 24.dp else 7.dp).height(5.dp)
                            .background(if(i==index)Color.White else Color.White.copy(alpha=.38f))) }
                    }
                    Row(horizontalArrangement=Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick=onPrev,modifier=Modifier.size(31.dp)) {Icon(Icons.Outlined.ChevronLeft,"Anterior",tint=Color.White)}
                        IconButton(onClick=onNext,modifier=Modifier.size(31.dp)) {Icon(Icons.Outlined.ChevronRight,"Siguiente",tint=Color.White)}
                    }
                }
            }
        }
    }
}
