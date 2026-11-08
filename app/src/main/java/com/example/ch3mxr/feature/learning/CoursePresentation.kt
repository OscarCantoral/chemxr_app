package com.example.ch3mxr.feature.learning

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.components.*
import com.example.ch3mxr.core.ui.theme.ChemShapes
import com.example.ch3mxr.core.ui.theme.LocalChemIsDark
import com.example.ch3mxr.feature.experience.*
import com.example.ch3mxr.feature.main.components.*
import com.example.ch3mxr.feature.main.model.DemoCourse
import com.example.ch3mxr.feature.selection.formatCoursePrice
import java.util.Locale

/** Usa los dos vectores que ya existen en el proyecto del usuario, sin sobrescribirlos. */
@Suppress("DiscouragedApi")
@Composable
fun AdromAuthorMark(modifier: Modifier = Modifier) {
    val context=LocalContext.current
    val dark=LocalChemIsDark.current
    val name=if(dark) "adrom_logo" else "adrom_logo_2"
    val resource=remember(context,name) {context.resources.getIdentifier(name,"drawable",context.packageName)}
    if(resource!=0) Image(painterResource(resource),"ADROM",modifier.width(96.dp).height(30.dp))
    else Text("ADROM",modifier,fontWeight=FontWeight.ExtraBold,color=MaterialTheme.colorScheme.primary)
}

@Composable
fun CourseAuthor(course: DemoCourse) {
    if(course.owner==CourseOwner.ADROM) {
        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(7.dp)) {
            AdromAuthorMark()
        }
    } else Text(course.instructor,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
}

fun accessLabel(course:DemoCourse,isPro:Boolean,purchased:Boolean):String = when {
    course.access==CourseAccess.PURCHASE && purchased -> "Comprado"
    course.access==CourseAccess.PURCHASE -> formatCoursePrice(course.priceCents)
    course.access==CourseAccess.PRO -> if(isPro) "Incluido en PRO" else "Exclusivo PRO"
    else -> "Gratis · ADROM"
}

@Composable
fun LearningCourseCard(course:DemoCourse,onOpen:()->Unit,modifier:Modifier=Modifier,compact:Boolean=false) {
    val store=LocalLearningStore.current
    val colors=MaterialTheme.colorScheme
    val savedId="course:${course.id}"
    ChemPanel(modifier) {
        Box(Modifier.fillMaxWidth().height(if(compact) 112.dp else 145.dp).background(course.tileColor)) {
            Icon(course.icon,null,Modifier.size(62.dp).align(Alignment.Center),tint=course.iconColor)
            IconButton(onClick={store.toggleShortcut(SavedShortcut(savedId,"personal",course.title,"Curso","learnCourse/${course.id}"))},
                modifier=Modifier.align(Alignment.TopEnd).size(48.dp)) {
                Icon(if(store.isSaved(savedId)) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                    if(store.isSaved(savedId)) "Quitar curso de Mi estante" else "Guardar curso en Mi estante",tint=course.iconColor)
            }
            if(course.access==CourseAccess.PRO) Text("PRO",Modifier.align(Alignment.BottomStart).padding(8.dp),
                color=course.iconColor,fontWeight=FontWeight.Bold)
        }
        Spacer(Modifier.height(9.dp))
        Text(course.area,style=MaterialTheme.typography.labelSmall,color=colors.onSurfaceVariant)
        Text(course.title,style=MaterialTheme.typography.titleSmall,fontWeight=FontWeight.Bold)
        CourseAuthor(course)
        Text("${course.level} · ${course.lessons} clases · ${course.duration}",style=MaterialTheme.typography.labelSmall,color=colors.onSurfaceVariant)
        Text("★ ${String.format(Locale.US,"%.1f",course.rating)} · ${course.ratingsCount} valoraciones",
            style=MaterialTheme.typography.labelSmall,color=colors.onSurfaceVariant)
        Text(accessLabel(course,store.isPro,course.id in store.purchasedIds),fontWeight=FontWeight.Bold,color=colors.primary)
        if(course.progress>0f) {
            ProgressStrip(course.progress,Modifier.fillMaxWidth())
            Text("${(course.progress*100).toInt()} % completado",style=MaterialTheme.typography.labelSmall)
        }
        Spacer(Modifier.height(8.dp))
        ChemButton("Ver curso",onOpen,Modifier.fillMaxWidth(),style=ChemButtonStyle.OUTLINED)
    }
}

@Composable
fun LearningCourseDetail(courseId:String,modifier:Modifier=Modifier,onOpen:(String)->Unit) {
    val store=LocalLearningStore.current
    val course=LearningCatalog.find(courseId)
    if(course==null) {MainPage(modifier) {Heading("Curso no disponible.")};return}
    val accessible=store.canStudy(course)
    MainPage(modifier) {
        Eyebrow(if(course.owner==CourseOwner.ADROM) "Cursos ADROM" else "Cursos de docentes")
        Heading(course.title)
        Box(Modifier.fillMaxWidth().height(175.dp).background(course.tileColor),contentAlignment=Alignment.Center) {
            Icon(course.icon,null,Modifier.size(72.dp),tint=course.iconColor)
        }
        DemoBadge(accessLabel(course,store.isPro,course.id in store.purchasedIds))
        GroupPanel(if(course.owner==CourseOwner.ADROM) "Creado por ADROM" else "Docente", "Autor del curso") {CourseAuthor(course)}
        Text("${course.level} · ${course.lessons} clases · ${course.duration}")
        Text("★ ${String.format(Locale.US,"%.1f",course.rating)} · ${course.ratingsCount} valoraciones")
        Text("Aprende con explicaciones, ejercicios y recursos organizados por módulos.")
        if(course.access==CourseAccess.PURCHASE) {
            Text(if(course.id in store.purchasedIds) "Este curso ya está en tu aprendizaje." else "Compra individual disponible para estudiantes gratuitos y PRO.")
        }
        if(accessible) {
            ChemButton(if(course.id in store.enrolledIds) "Continuar curso" else "Empezar curso",{
                if(store.enroll(course)) onOpen("learningContent/${course.id}")
            },Modifier.fillMaxWidth())
        } else if(course.access==CourseAccess.PRO) {
            Text("Este curso avanzado de ADROM está incluido en la suscripción PRO.")
            ChemButton("Pásate a PRO",{store.setPlan(StudentPlan.PRO)},Modifier.fillMaxWidth())
        } else {
            ChemButton(if(course.id in store.cartIds) "Ver mi selección" else "Añadir a mi selección",{
                store.addToCart(course.id);onOpen("selection")
            },Modifier.fillMaxWidth())
        }
        ChemButton(if(store.isSaved("course:${course.id}")) "Quitar de Mi estante" else "Guardar en Mi estante",{
            store.toggleShortcut(SavedShortcut("course:${course.id}","personal",course.title,"Curso","learnCourse/${course.id}"))
        },Modifier.fillMaxWidth(),style=ChemButtonStyle.OUTLINED)
        GroupPanel("Contenido", "Vista del programa") {
            Text("01 · Fundamentos y conceptos")
            Text("02 · Ejemplos y práctica guiada")
            Text("03 · Proyecto de aplicación")
        }
    }
}

@Composable
fun LearningContentPage(courseId:String,modifier:Modifier=Modifier,onOpen:(String)->Unit) {
    val store=LocalLearningStore.current
    val course=LearningCatalog.find(courseId)
    if(course==null || !store.canStudy(course)) {
        MainPage(modifier) {Heading("Revisa tu acceso.");ChemButton("Ver curso",{onOpen("learnCourse/$courseId")},Modifier.fillMaxWidth())}
        return
    }
    MainPage(modifier) {
        Eyebrow("Tu aprendizaje")
        Heading(course.title)
        CourseAuthor(course)
        GroupPanel("Módulo 1 · Introducción", "Lección de demostración") {
            Icon(Icons.Outlined.PlayCircleOutline,null,Modifier.fillMaxWidth().height(130.dp),tint=MaterialTheme.colorScheme.primary)
            Text("Reconoce los conceptos principales y resuelve la primera actividad del curso.")
        }
        DemoNavRow("Recursos de aprendizaje","Consulta la biblioteca colaborativa",Icons.Outlined.LocalLibrary) {onOpen("library")}
        DemoNavRow("Mis apuntes","Texto, imágenes y referencias privadas",Icons.Outlined.EditNote) {onOpen("notes")}
        DemoNavRow("Comunidad","Preguntas, respuestas y votaciones",Icons.Outlined.Groups) {onOpen("communityDetail")}
    }
}

@Composable
fun StudentPlanPage(modifier:Modifier=Modifier) {
    val store=LocalLearningStore.current
    MainPage(modifier) {
        Eyebrow("Mi plan ChemXR")
        Heading(if(store.isPro) "Ya eres PRO." else "Aprende con PRO.")
        DemoBadge(if(store.isPro) "PRO activo" else "Plan gratuito")
        GroupPanel("Gratuito", "Aprendizaje abierto") {
            Text("• Cursos ADROM gratuitos")
            Text("• Compra individual de cursos de docentes")
            Text("• Biblioteca, comunidades y estante")
            Text("• Con anuncios y sin TutorIA")
        }
        GroupPanel("PRO", "Suscripción de ChemXR") {
            Text("• TutorIA")
            Text("• Experiencia sin anuncios")
            Text("• Cursos avanzados ADROM exclusivos")
            Text("• Todos los beneficios del plan gratuito")
        }
        Text("Los cursos de docentes con precio se compran por separado en ambos planes.")
        Text("Activación de demostración: no se realizará ningún cobro.",style=MaterialTheme.typography.bodySmall)
        ChemButton(if(store.isPro) "Volver a Gratuito · Demo" else "Pásate a PRO",{
            store.setPlan(if(store.isPro) StudentPlan.FREE else StudentPlan.PRO)
        },Modifier.fillMaxWidth())
        Text("La matrícula y los permisos de una institución se administran por separado.",style=MaterialTheme.typography.bodySmall)
    }
}
