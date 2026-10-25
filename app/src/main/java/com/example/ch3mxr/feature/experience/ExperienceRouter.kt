package com.example.ch3mxr.feature.experience

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.components.ChemButton
import com.example.ch3mxr.feature.main.components.*
import com.example.ch3mxr.feature.profile.model.StudySpace

private val pageTitles = mapOf(
    "enrollment" to "Matrícula", "record" to "Mi progreso", "grades" to "Notas", "attendance" to "Asistencias",
    "finance" to "Cuenta corriente", "documents" to "Documentos oficiales", "procedures" to "Trámites",
    "services" to "Servicios", "sessions" to "Sesiones", "surveys" to "Encuestas",
    "course" to "Tu curso", "modules" to "Contenido", "syllabus" to "Sílabo", "labs" to "Laboratorios",
    "assignments" to "Evaluaciones", "rubric" to "Rúbrica", "team" to "Mi equipo", "quiz" to "Examen",
    "simulation" to "Simulador", "interactive" to "Interactivo 2D", "model3d" to "Visor 3D",
    "library" to "Biblioteca", "shelf" to "Mi estante", "notes" to "Apuntes", "submitResource" to "Compartir recurso",
    "communities" to "Comunidad", "communityList" to "Comunidades", "communityDetail" to "Publicaciones",
    "channels" to "Canales", "messages" to "Mensajes", "forum" to "Foro del curso", "teacher" to "Docente",
    "reviews" to "Reseñas", "achievements" to "Logros", "jobs" to "Bolsa de trabajo", "support" to "Soporte",
    "privateClass" to "Crear aula privada", "studentCard" to "Carnet virtual", "membership" to "Mi membresía", "personalData" to "Datos personales", "adVideo" to "Publicidad · DEMO"
)
fun experienceTitle(route: String): String = pageTitles[route] ?: "ChemXR"

@Composable
fun ExperiencePageScreen(route:String,space:StudySpace,period:String, modifier:Modifier=Modifier,onOpen:(String)->Unit) {
    when(route) {
        "enrollment" -> EnrollmentPage(space,period,modifier,onOpen)
        "record" -> RecordPage(space,modifier,onOpen)
        "grades" -> GradesPage(modifier)
        "attendance" -> AttendancePage(modifier)
        "finance" -> FinancePage(modifier)
        "documents" -> DocumentsPage(space,modifier)
        "procedures" -> ProceduresPage(modifier)
        "services" -> ServicesPage(modifier)
        "sessions" -> SessionsPage(modifier)
        "surveys" -> SurveysPage(modifier)
        "course" -> CourseOverviewPage(space,modifier,onOpen)
        "modules" -> ModulesPage(space,modifier,onOpen)
        "syllabus" -> SyllabusPage(space,modifier)
        "labs" -> LabsPage(modifier,onOpen)
        "assignments" -> AssignmentPage(modifier,onOpen)
        "rubric" -> RubricPage(modifier)
        "team" -> TeamPage(modifier,onOpen)
        "quiz" -> QuizPage(modifier)
        "simulation" -> SimulationPage(modifier)
        "interactive" -> InteractivePage(modifier)
        "model3d" -> Model3DPage(modifier)
        "library" -> LibraryPage(space,modifier,onOpen)
        "shelf" -> ShelfPage(modifier,onOpen)
        "notes" -> NotesPage(modifier)
        "submitResource" -> SubmitResourcePage(modifier)
        "communities" -> CommunityHubPage(space,modifier,onOpen)
        "communityList" -> CommunitiesPage(space,modifier,onOpen)
        "communityDetail" -> CommunityDetailPage(space,modifier,onOpen)
        "channels" -> ChannelsPage(space,modifier)
        "messages" -> MessagesPage(modifier)
        "forum" -> ForumPage(modifier)
        "teacher" -> TeacherPage(modifier,onOpen)
        "reviews" -> ReviewsPage(modifier)
        "achievements" -> AchievementsPage(modifier)
        "privateClass" -> PrivateClassPage(modifier)
        "studentCard" -> StudentCardPage(space,period,modifier)
        "membership" -> MembershipPage(space,modifier)
        "personalData" -> PersonalDataPage(modifier)
        "adVideo" -> SponsoredVideoPage(modifier)
        "jobs" -> GenericMockPage("Bolsa de trabajo", "Ofertas, prácticas, convenios y postulaciones", listOf("Prácticas profesionales", "Convocatorias", "Mis postulaciones"),modifier)
        "support" -> GenericMockPage("Soporte institucional", "Tickets, incidencias y solicitudes de ayuda", listOf("Nuevo ticket", "Mis solicitudes", "Centro de ayuda"),modifier)
        else -> GenericMockPage("ChemXR", "Pantalla de demostración", listOf("Sin contenido asignado"),modifier)
    }
}

@Composable
private fun GenericMockPage(title:String, subtitle:String,items:List<String>,modifier:Modifier=Modifier) {
    MainPage(modifier) {
        Eyebrow("Módulo institucional")
        Heading(title)
        Text(subtitle,color=MaterialTheme.colorScheme.onSurfaceVariant)
        MockNote()
        items.forEach { DemoNavRow(it,"Pendiente de integración con ChemXR API") {} }
    }
}

@Composable
private fun StudentCardPage(space:StudySpace,period:String,modifier:Modifier) {
    MainPage(modifier) {
        SpaceContextHeader(space,"Carnet institucional · Solo maqueta")
        Heading("Carnet virtual.")
        GroupPanel("Credencial digital · DEMO", "No constituye un documento de identidad válido") {
            SimpleInfo("Estudiante","Oscar Cantoral (DEMO)")
            SimpleInfo("Código",space.studentCode)
            SimpleInfo("Programa",space.program)
            SimpleInfo("Sede",space.campus)
            SimpleInfo("Periodo",period)
            DemoBadge("CARNET DE DEMOSTRACIÓN")
            Text("El QR institucional válido se implementará con el backend y autorización correspondiente.")
        }
    }
}

@Composable
private fun MembershipPage(space:StudySpace,modifier:Modifier) {
    MainPage(modifier) {
        Eyebrow("Planes y accesos")
        Heading("Mis membresías.")
        GroupPanel("Mi espacio · Plan gratuito", "Plan de ChemXR") {
            SimpleInfo("Estado","Activo · DEMO")
            SimpleInfo("Límites","1 aula temporal · 3 participantes")
            DemoBadge("Gratuito")
        }
        if(space.academic) GroupPanel("Afiliación a ${space.name}", "Asignación institucional simulada") {
            SimpleInfo("Rol", "Estudiante")
            SimpleInfo("Programa",space.program)
            SimpleInfo("Institución",space.name)
            DemoBadge("Acceso institucional de ejemplo")
        }
        Text("La membresía de ChemXR y la matrícula de una institución son independientes.")
    }
}

@Composable
private fun PersonalDataPage(modifier:Modifier) {
    val context=LocalContext.current
    val prefs=remember {context.getSharedPreferences("chemxr_demo_profile", Context.MODE_PRIVATE)}
    var biography by remember {mutableStateOf(prefs.getString("bio","Ciencia, tecnología y aprendizaje") ?: "")}
    var interests by remember {mutableStateOf(prefs.getString("interests","Ciencia, tecnología, música") ?: "")}
    var country by remember {mutableStateOf(prefs.getString("country","Perú") ?: "Perú")}
    var department by remember {mutableStateOf(prefs.getString("department","Lima") ?: "Lima")}
    var province by remember {mutableStateOf(prefs.getString("province","Lima") ?: "Lima")}
    var district by remember {mutableStateOf(prefs.getString("district","Miraflores") ?: "Miraflores")}
    var saved by remember {mutableStateOf(false)}
    val departments=if(country=="Perú") listOf("Lima","Arequipa","Cusco") else listOf("Otra región")
    val provinces=when(department) {
        "Lima" -> listOf("Lima","Huaral","Cañete")
        "Arequipa" -> listOf("Arequipa","Camaná")
        "Cusco" -> listOf("Cusco","Urubamba")
        else -> listOf("Otra provincia")
    }
    val districts=when(province) {
        "Lima" -> listOf("Miraflores","Santiago de Surco","Lima")
        "Arequipa" -> listOf("Cercado","Cayma")
        "Cusco" -> listOf("Cusco","San Sebastián")
        else -> listOf("Centro","Otro distrito")
    }
    MainPage(modifier) {
        Eyebrow("Perfil · Preferencias de identidad")
        Heading("Datos personales.")
        GroupPanel("Presentación", "Solo demostración local; no modifica tu identidad OAuth") {
            OutlinedTextField(biography,{biography=it;saved=false},label={Text("Biografía")},modifier=Modifier.fillMaxWidth(),minLines=2)
            OutlinedTextField(interests,{interests=it;saved=false},label={Text("Intereses")},modifier=Modifier.fillMaxWidth())
        }
        GroupPanel("Ubicación", "Catálogos locales de ejemplo; sustituir por app.core_* desde API") {
            LocationChoice("País",country,listOf("Perú","Chile","Colombia")) {
                country=it;department=if(it=="Perú")"Lima" else "Otra región";province="";district="";saved=false
            }
            LocationChoice("Departamento / región",department,departments) {
                department=it;province="";district="";saved=false
            }
            LocationChoice("Provincia",province,provinces) {
                province=it;district="";saved=false
            }
            LocationChoice("Distrito",district,districts) { district=it;saved=false }
        }
        ChemButton("Guardar preferencias",{
            prefs.edit().putString("bio",biography).putString("interests",interests)
                .putString("country",country).putString("department",department)
                .putString("province",province).putString("district",district).apply()
            saved=true
        },Modifier.fillMaxWidth())
        if(saved) DemoBadge("Preferencias guardadas en este teléfono")
        MockNote()
    }
}

@Composable
private fun LocationChoice(title:String,value:String,options:List<String>,onSelect:(String)->Unit) {
    var expanded by remember {mutableStateOf(false)}
    Box(Modifier.fillMaxWidth()) {
        OutlinedButton(onClick={expanded=true},modifier=Modifier.fillMaxWidth(),shape=com.example.ch3mxr.core.ui.theme.ChemShapes.Component) {
            Text("$title: ${value.ifBlank {"Elegir"}}",modifier=Modifier.weight(1f))
            Icon(Icons.Outlined.KeyboardArrowDown,"Desplegar")
        }
        DropdownMenu(expanded=expanded,onDismissRequest={expanded=false}) {
            options.forEach { option -> DropdownMenuItem(text={Text(option)},onClick={onSelect(option);expanded=false}) }
        }
    }
}

@Composable
private fun SponsoredVideoPage(modifier:Modifier=Modifier) {
    MainPage(modifier) {
        Eyebrow("Publicidad patrocinada · Maqueta")
        Heading("Anuncio de video.")
        GroupPanel("Reproductor a pantalla completa", "Sin archivo publicitario adjunto") {
            Icon(Icons.Outlined.SmartDisplay, null, Modifier.fillMaxWidth().height(230.dp), tint=MaterialTheme.colorScheme.primary)
            Text("La pantalla de destino está preparada. Cuando exista un video autorizado, el reproductor de medios podrá cargarlo desde la API/CDN.")
        }
    }
}
