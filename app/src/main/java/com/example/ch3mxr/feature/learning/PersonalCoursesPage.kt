package com.example.ch3mxr.feature.learning

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.components.*
import com.example.ch3mxr.core.ui.theme.ChemShapes
import com.example.ch3mxr.feature.ads.NativeAdPlacement
import com.example.ch3mxr.feature.ads.NativeAdSlot
import com.example.ch3mxr.feature.main.components.*

@Composable
fun PersonalCoursesPage(modifier:Modifier=Modifier,onOpen:(String)->Unit) {
    val store=LocalLearningStore.current
    var section by rememberSaveable {mutableStateOf("Mis cursos")}
    var progress by rememberSaveable {mutableStateOf("Todos")}
    val source=when(section) {"Catálogo"->LearningCatalog.all;"ADROM PRO"->LearningCatalog.pro;else->store.myCourses()}
    val courses=source.filter {when(progress) {"En curso"->it.progress>0f && it.progress<1f;"Completados"->it.progress>=1f;else->true}}
    MainLazyPage(modifier) {
        item {Eyebrow("Mi espacio · Tu aprendizaje");Heading("Un paso más hacia tu objetivo.")}
        item {Row(horizontalArrangement=Arrangement.spacedBy(7.dp)) {
            ChemButton("Mi estante",{onOpen("shelf")},Modifier.weight(1f),style=ChemButtonStyle.OUTLINED)
            ChemButton("Explorar",{onOpen("explore")},Modifier.weight(1f),style=ChemButtonStyle.OUTLINED)
        }}
        item {Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
            listOf("Mis cursos","ADROM PRO","Catálogo").forEach {value->FilterChip(selected=section==value,onClick={section=value},label={Text(value)},shape=ChemShapes.Component)}
        }}
        item {Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
            listOf("Todos","En curso","Completados").forEach {value->FilterChip(selected=progress==value,onClick={progress=value},label={Text(value)},shape=ChemShapes.Component)}
        }}
        if(store.adsAllowed("personal")) item {NativeAdSlot(NativeAdPlacement.COURSES)}
        if(courses.isEmpty()) item {Text("No hay cursos para estos filtros. Explora el catálogo para empezar uno.")}
        items(courses,key={it.id}) {course->LearningCourseCard(course,{onOpen("learnCourse/${course.id}")},compact=true)}
    }
}
