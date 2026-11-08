package com.example.ch3mxr.feature.explore

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.components.ChemButton
import com.example.ch3mxr.core.ui.theme.ChemShapes
import com.example.ch3mxr.feature.ads.NativeAdPlacement
import com.example.ch3mxr.feature.ads.NativeAdSlot
import com.example.ch3mxr.feature.learning.*
import com.example.ch3mxr.feature.main.components.Heading

@Composable
fun ExploreCoursesScreen(onSelection:()->Unit,onOpenCourse:(String)->Unit,modifier:Modifier=Modifier) {
    val store=LocalLearningStore.current
    var filter by rememberSaveable {mutableStateOf("Todos")}
    val courses=LearningCatalog.all.filter {when(filter) {
        "ADROM gratis"->it.owner==CourseOwner.ADROM && it.access==CourseAccess.FREE
        "ADROM PRO"->it.access==CourseAccess.PRO
        "De docentes"->it.access==CourseAccess.PURCHASE
        else->true
    }}
    LazyVerticalGrid(columns=GridCells.Fixed(2),modifier=modifier.navigationBarsPadding(),
        contentPadding=PaddingValues(15.dp),horizontalArrangement=Arrangement.spacedBy(12.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
        item(key="intro",span={GridItemSpan(maxLineSpan)}) {Column(verticalArrangement=Arrangement.spacedBy(8.dp)) {
            Heading("Encuentra tu próximo curso.",26)
            Text("Cursos ADROM y de docentes · Precios en soles")
            Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                listOf("Todos","ADROM gratis","ADROM PRO","De docentes").forEach {value->
                    FilterChip(selected=filter==value,onClick={filter=value},label={Text(value)},shape=ChemShapes.Component)
                }
            }
        }}
        courses.forEachIndexed {index,course->
            item(key=course.id) {LearningCourseCard(course,{onOpenCourse(course.id)},compact=true)}
            if(index==3 && store.adsAllowed("personal")) item(key="programs_ad",span={GridItemSpan(maxLineSpan)}) {NativeAdSlot(NativeAdPlacement.EXPLORE)}
        }
        item(key="selection",span={GridItemSpan(maxLineSpan)}) {
            ChemButton("Mi selección (${store.cartIds.size})",onSelection,Modifier.fillMaxWidth())
        }
    }
}
