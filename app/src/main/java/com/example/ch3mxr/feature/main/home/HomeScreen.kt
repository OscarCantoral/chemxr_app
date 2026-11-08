package com.example.ch3mxr.feature.main.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.components.ChemButton
import com.example.ch3mxr.core.ui.components.ChemButtonStyle
import com.example.ch3mxr.feature.ads.HomeAdCarousel
import com.example.ch3mxr.feature.experience.*
import com.example.ch3mxr.feature.learning.*
import com.example.ch3mxr.feature.main.components.*

@Composable
fun HomeScreen(modifier:Modifier=Modifier,onOpen:(String)->Unit={}) {
    val store=LocalLearningStore.current
    MainLazyPage(modifier) {
        item {Eyebrow("Mi espacio · ${if(store.isPro) "PRO" else "Gratuito"}")}
        item {Heading("Hola, Oscar.",31);Text("¿Qué vas a descubrir hoy?",color=MaterialTheme.colorScheme.onSurfaceVariant)}
        item {LearningCourseCard(LearningCatalog.free.first(),{onOpen("learnCourse/adrom_chemistry")})}
        item {DemoNavRow("Biblioteca abierta","Archivos, enlaces y recursos compartidos por la comunidad",Icons.Outlined.LocalLibrary) {onOpen("library")}}
        if(store.adsAllowed("personal")) item {HomeAdCarousel()}
        item {SectionHeading("Explora algo nuevo","Explorar") {onOpen("explore")}}
        items(LearningCatalog.all.filterNot {it.id=="adrom_chemistry"}.take(6),key={it.id}) {course->LearningCourseCard(course,{onOpen("learnCourse/${course.id}")},compact=true)}
        item {SectionHeading("Tu aprendizaje")}
        item {DemoNavRow("Mi estante","Accesos directos a tus favoritos",Icons.Outlined.Bookmarks) {onOpen("shelf")}}
        item {DemoNavRow("Comunidad","Preguntas, recursos y votaciones",Icons.Outlined.Groups) {onOpen("communityDetail")}}
        if(!store.isPro) item {ChemButton("Conoce PRO",{onOpen("membership")},Modifier.fillMaxWidth(),style=ChemButtonStyle.OUTLINED)}
    }
}
