package com.example.ch3mxr.feature.experience

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ch3mxr.core.ui.components.*
import com.example.ch3mxr.feature.main.components.*
import com.example.ch3mxr.feature.profile.model.StudySpace

private data class Subject(val name:String, val credits:Int, val hours:Int, val prerequisite:String)
private val availableSubjects = listOf(
    Subject("Redes y Comunicación de Datos II",4,5,"Redes I"),
    Subject("Mecánica Clásica",4,5,"Física básica"),
    Subject("Formación para la Investigación",3,4,"Ninguno"),
    Subject("Fundamentos de Marketing",3,4,"Ninguno"),
    Subject("Ética Profesional",2,2,"Ninguno")
)

@Composable
fun EnrollmentPage(space: StudySpace, period: String, modifier: Modifier = Modifier, onOpen:(String)->Unit) {
    val selected = DemoInteractions.selectedEnrollments
    val courses = remember { availableSubjects }
    val credits = courses.filter { it.name in selected }.sumOf { it.credits }
    val hours = courses.filter { it.name in selected }.sumOf { it.hours }
    MainPage(modifier) {
        SpaceContextHeader(space, "Matrícula · $period")
        Heading("Elige tus asignaturas.")
        MockNote()
        GroupPanel("Oferta académica", "La oferta y los requisitos son simulados. No registra una matrícula real.") {
            courses.forEach { subject ->
                val checked = subject.name in selected
                Row(Modifier.fillMaxWidth().clickable { if (checked) selected.remove(subject.name) else selected.add(subject.name) }.padding(vertical=9.dp), verticalAlignment=Alignment.CenterVertically) {
                    Checkbox(checked = checked, onCheckedChange = { if (it) selected.add(subject.name) else selected.remove(subject.name) })
                    Column(Modifier.weight(1f)) {
                        Text(subject.name, style=MaterialTheme.typography.bodyMedium, fontWeight=FontWeight.SemiBold)
                        Text("${subject.credits} créditos · ${subject.hours} h/semana · Requisito: ${subject.prerequisite}", style=MaterialTheme.typography.labelSmall)
                    }
                }
                HorizontalDivider(color=MaterialTheme.colorScheme.outline.copy(alpha=.16f))
            }
        }
        GroupPanel("Resumen de preselección", "El costo oficial depende de políticas y escalas de tu institución.") {
            SimpleInfo("Cursos", selected.size.toString())
            SimpleInfo("Créditos", credits.toString())
            SimpleInfo("Horas semanales", hours.toString())
            SimpleInfo("Costo", "Por determinar (DEMO)")
            ChemButton("Revisar cuenta corriente", { onOpen("finance") }, Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Text("La confirmación definitiva necesitará validar vacantes, prerrequisitos, horarios, pagos y periodo desde ChemXR API.", style=MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun RecordPage(space:StudySpace, modifier:Modifier=Modifier, onOpen:(String)->Unit) {
    MainPage(modifier) {
        SpaceContextHeader(space, "Historial del estudiante")
        Heading("Tu avance académico.")
        GroupPanel("Plan de estudios", "Malla curricular y créditos · datos de demostración") {
            SimpleInfo("Programa", space.program)
            SimpleInfo("Créditos aprobados", "48 de 180 · DEMO")
            SimpleInfo("Asignaturas aprobadas", "14")
            SimpleInfo("Asignaturas pendientes", "34")
            LinearProgressIndicator(progress={.267f}, modifier=Modifier.fillMaxWidth())
        }
        DemoNavRow("Récord de notas", "Calificaciones y periodos", Icons.Outlined.Assessment) { onOpen("grades") }
        DemoNavRow("Asistencias", "Asistencia por curso y sesión", Icons.Outlined.HowToReg) { onOpen("attendance") }
        DemoNavRow("Insignias y certificados", "Constancias y logros compartibles", Icons.Outlined.WorkspacePremium) { onOpen("achievements") }
    }
}

@Composable
fun GradesPage(modifier:Modifier=Modifier) {
    MainPage(modifier) {
        Eyebrow("Reporte institucional")
        Heading("Calificaciones.")
        MockNote()
        listOf(Triple("Mecánica Clásica", "Práctica calificada", "17/20"), Triple("Redes II", "Laboratorio", "18/20"), Triple("Formación para la Investigación", "Avance de informe", "Por calificar")).forEach { (course,work,result) ->
            GroupPanel(course,work) { SimpleInfo("Resultado",result); SimpleInfo("Estado","Ejemplo") }
        }
    }
}

@Composable
fun AttendancePage(modifier:Modifier=Modifier) {
    MainPage(modifier) {
        Eyebrow("Asistencia por curso")
        Heading("Asistencias.")
        listOf("Redes y Comunicación de Datos II" to 0.90f, "Mecánica Clásica" to 0.85f, "Formación para la Investigación" to 1f).forEach { (name,pct) ->
            GroupPanel(name, "Sesiones registradas · DEMO") {
                LinearProgressIndicator(progress={pct}, modifier=Modifier.fillMaxWidth())
                Spacer(Modifier.height(6.dp))
                Text("${(pct*100).toInt()} % · incluye faltas y tardanzas en el historial real")
            }
        }
    }
}

@Composable
fun FinancePage(modifier:Modifier=Modifier) {
    MainPage(modifier) {
        Eyebrow("Cuenta corriente institucional")
        Heading("Pagos y beneficios.")
        MockNote()
        GroupPanel("Cuotas del periodo", "Montos ilustrativos; no representan deudas reales") {
            listOf("Cuota 1" to "S/ 390.00 · Ejemplo", "Cuota 2" to "S/ 390.00 · Ejemplo", "Cuota 3" to "Por vencer · Ejemplo").forEach { (title,desc) ->
                SimpleInfo(title,desc)
            }
        }
        DemoNavRow("Comprobantes y constancias", "Descargas sujetas a documentos emitidos", Icons.Outlined.ReceiptLong) {}
        DemoNavRow("Becas, convenios y descuentos", "Solicitud y vigencia de beneficios", Icons.Outlined.Savings) {}
        DemoNavRow("Historial de pagos", "Transacciones y abonos", Icons.Outlined.History) {}
        GroupPanel("Condición económica", "No se consulta ni se cobra dinero en este prototipo") {
            Text("Los pagos reales requieren pasarela autorizada, validación institucional y comprobantes oficiales.", style=MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun DocumentsPage(space: StudySpace, modifier:Modifier=Modifier) {
    MainPage(modifier) {
        SpaceContextHeader(space, "Normativa y documentos vigentes")
        Heading("Documentos de ${space.name}.")
        MockNote()
        listOf("Reglamento de estudios","Código de ética", "Guía del estudiante", "Reglamento de biblioteca", "Política de datos personales", "Cronograma académico", "Manual de matrícula", "Normas de evaluación").forEach { title ->
            var expanded by rememberSaveable(title,space.id) { mutableStateOf(false) }
            DemoNavRow(title, "Documento de referencia · archivo PDF pendiente", Icons.Outlined.PictureAsPdf) { expanded = !expanded }
            if (expanded) GroupPanel("$title · DEMO", "El servidor institucional proporcionará el PDF, versión, fecha de vigencia y aceptación cuando corresponda.") {
                DemoBadge("Archivo no disponible en maqueta")
            }
        }
    }
}

@Composable
fun ProceduresPage(modifier:Modifier=Modifier) {
    var chosen by rememberSaveable { mutableStateOf("Rectificación de nota") }
    var message by rememberSaveable { mutableStateOf("") }
    var sent by rememberSaveable { mutableStateOf(false) }
    MainPage(modifier) {
        Eyebrow("Gestiones institucionales")
        Heading("Trámites y solicitudes.")
        MockNote()
        GroupPanel("Catálogo de trámites", "Elige un procedimiento para completar una solicitud de ejemplo") {
            listOf("Rectificación de nota","Retiro de curso","Constancia de matrícula","Cambio de modalidad", "Beca o convenio", "Reincorporación", "Cambio de sede").forEach { label ->
                FilterChip(selected = chosen == label, onClick = { chosen=label; sent=false }, label={ Text(label) }, shape=com.example.ch3mxr.core.ui.theme.ChemShapes.Component, modifier=Modifier.padding(end=5.dp,bottom=5.dp))
            }
        }
        GroupPanel("Solicitud: $chosen", "Adjunta evidencia desde el portal real cuando la API esté conectada") {
            OutlinedTextField(value=message,onValueChange={message=it;sent=false},label={Text("Detalle de tu solicitud")},modifier=Modifier.fillMaxWidth(),minLines=3)
            Spacer(Modifier.height(9.dp))
            ChemButton("Registrar solicitud DEMO",{ sent=true },modifier=Modifier.fillMaxWidth())
            if(sent) Text("Solicitud simulada registrada en esta pantalla. No se envió a ${"la institución"}.", color=MaterialTheme.colorScheme.primary)
        }
        GroupPanel("Estado de trámites", "Historial ilustrativo") {
            SimpleInfo("Constancia de estudios", "Atendida · DEMO")
            SimpleInfo("Validación de documentos", "En revisión · DEMO")
        }
    }
}

@Composable
fun ServicesPage(modifier:Modifier=Modifier) {
    val booked = DemoInteractions.bookedResources
    var date by rememberSaveable { mutableStateOf("2026-10-25") }
    var startTime by rememberSaveable { mutableStateOf("16:00") }
    var endTime by rememberSaveable { mutableStateOf("17:00") }
    MainPage(modifier) {
        Eyebrow("Recursos físicos · Servicios")
        Heading("Servicios del campus.")
        MockNote()
        GroupPanel("Horario de solicitud", "Reserva ilustrativa: comprueba la disponibilidad real con la institución") {
            OutlinedTextField(date,{date=it},label={Text("Fecha AAAA-MM-DD")},modifier=Modifier.fillMaxWidth())
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(startTime,{startTime=it},label={Text("Desde HH:MM")},modifier=Modifier.weight(1f))
                OutlinedTextField(endTime,{endTime=it},label={Text("Hasta HH:MM")},modifier=Modifier.weight(1f))
            }
        }
        listOf("Reservar cubículo","Sala de conferencias","Préstamo de laptop","Préstamo de tablet","Biblioteca física","Laboratorio de electrónica","Comedor","Gimnasio","Soporte técnico").forEach { item ->
            GroupPanel(item,"Disponibilidad simulada · sujeta a normas y horarios") {
                SimpleInfo("Estado", if(item in booked) "Reserva DEMO solicitada" else "Disponible en maqueta")
                SimpleInfo("Horario de ejemplo", "$date · $startTime–$endTime")
                ChemButton(if(item in booked) "Cancelar prueba" else "Solicitar / Reservar", {
                    if(item in booked) booked.remove(item)
                    else if(date.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) && startTime < endTime) booked.add(item)
                }, Modifier.fillMaxWidth(), style=ChemButtonStyle.OUTLINED)
            }
        }
    }
}

@Composable
fun SessionsPage(modifier:Modifier=Modifier) {
    MainPage(modifier) {
        Eyebrow("Actividades docentes")
        Heading("Sesiones y grabaciones.")
        GroupPanel("Clase en directo", "Redes y Comunicación de Datos II · DEMO") {
            SimpleInfo("Plataforma", "Meet / Teams / Zoom")
            SimpleInfo("Fecha", "24/10/2026 · 20:15")
            DemoBadge("Enlace aún no asignado")
        }
        GroupPanel("Clases anteriores", "Grabaciones y transcripciones") {
            DemoNavRow("Sesión 10: Enrutamiento", "Video de ejemplo · Transcripción pendiente", Icons.Outlined.SmartDisplay) {}
            Spacer(Modifier.height(7.dp))
            DemoNavRow("Sesión 9: Redes LAN", "Material y notas de clase", Icons.Outlined.Description) {}
        }
    }
}

@Composable
fun SurveysPage(modifier:Modifier=Modifier) {
    var score by rememberSaveable { mutableIntStateOf(0) }
    var submitted by rememberSaveable { mutableStateOf(false) }
    MainPage(modifier) {
        Eyebrow("Calidad académica")
        Heading("Tu opinión importa.")
        GroupPanel("Evaluación del docente", "Encuesta anónima en su implementación real") {
            Text("¿Cómo evaluarías la claridad de las sesiones?")
            Row(horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                (1..5).forEach { n ->
                    FilterChip(selected=score==n,onClick={score=n; submitted=false},label={Text("$n")},shape=com.example.ch3mxr.core.ui.theme.ChemShapes.Component)
                }
            }
            ChemButton("Enviar respuesta DEMO",{ if(score>0) submitted=true },Modifier.fillMaxWidth())
            if(submitted) DemoBadge("Opinión registrada solo en esta pantalla")
        }
        DemoNavRow("Encuesta del curso","Materiales, actividades y carga académica",Icons.Outlined.Assignment) {}
        DemoNavRow("Satisfacción con el campus","Servicios e infraestructura",Icons.Outlined.RateReview) {}
    }
}

@Composable
fun SyllabusPage(space: StudySpace, modifier:Modifier=Modifier) {
    MainPage(modifier) {
        Eyebrow("Planificación del curso")
        Heading("Sílabo.")
        DemoBadge(if(space.academic) "Obligatorio en campus" else "Opcional en curso abierto")
        GroupPanel((if(space.academic) "Asignatura institucional" else "Química básica") + " · contenido ilustrativo", "Objetivos, metodología y evaluación") {
            SimpleInfo("Duración", "12 semanas")
            SimpleInfo("Módulos", "3")
            SimpleInfo("Resultados", "Comprender conceptos químicos")
            SimpleInfo("Evaluación", "Trabajos 40 % · Exámenes 60 %")
            Text("El documento oficial se descargará o visualizará cuando sea publicado.", style=MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun CourseOverviewPage(space:StudySpace,modifier:Modifier=Modifier,onOpen:(String)->Unit) {
    MainPage(modifier) {
        SpaceContextHeader(space, "Curso · Demostración")
        Heading(if(space.academic) "Redes y Comunicación de Datos II" else "Química básica")
        DemoBadge(if(space.academic) "Curso institucional" else "Acceso abierto")
        Text("Etiquetas: ciencia · tecnología · fundamentos",style=MaterialTheme.typography.labelSmall)
        GroupPanel("Docente", "Perfil público y mensajes") {
            DemoNavRow("Ana Morales", "Ver especialidad, cursos y contacto", Icons.Outlined.AccountCircle) { onOpen("teacher") }
        }
        GroupPanel("Tu progreso", "40 % completado · Ejemplo") {
            LinearProgressIndicator(progress={.4f}, modifier=Modifier.fillMaxWidth())
        }
        listOf(
            Triple("Contenido y recursos","Lecciones, PDFs, videos, multimedia", "modules"),
            Triple("Sílabo","Resultados de aprendizaje y cronograma", "syllabus"),
            Triple("Laboratorios virtuales","Simuladores activados por docente", "labs"),
            Triple("Exámenes y tareas","Intentos, rúbricas y consignas", "assignments"),
            Triple("Foro del curso","Preguntas, debates y respuestas", "forum"),
            Triple("Mi equipo de trabajo","Integrantes, líder y chat", "team"),
            Triple("Sesiones y grabaciones","Clases en línea y transcritas", "sessions"),
            Triple("Mis apuntes","Texto, imágenes y enlaces", "notes"),
            Triple("Calificaciones","Resultados y retroalimentación", "grades"),
            Triple("Reseñas del curso","Opiniones y valoración", "reviews")
        ).forEach { (title,desc,route) -> DemoNavRow(title,desc) { onOpen(route) } }
    }
}

@Composable
fun ModulesPage(space: StudySpace,modifier:Modifier=Modifier,onOpen:(String)->Unit) {
    MainPage(modifier) {
        Eyebrow("Programa del curso")
        Heading("Contenido y recursos.")
        (if(space.academic) listOf("01 · Introducción y teoría", "02 · Laboratorios y prácticas", "03 · Evaluación final") else listOf("01 · Fundamentos", "02 · Exploración y experiencias XR", "03 · Actividades y evaluación")).forEachIndexed { ix,name ->
            GroupPanel(name, "Submódulos · Recursos y prácticas") {
                DemoNavRow(if(ix==0) (if(space.academic) "Introducción a la asignatura" else "Introducción a Química básica") else "Experiencia interactiva", "Textos, PDF, audio y contenidos web", Icons.Outlined.MenuBook) { onOpen("interactive") }
                Spacer(Modifier.height(8.dp))
                DemoNavRow("Modelo tridimensional", "Visualización 3D sencilla · DEMO", Icons.Outlined.ViewInAr) { onOpen("model3d") }
            }
        }
    }
}

@Composable
fun AssignmentPage(modifier:Modifier=Modifier,onOpen:(String)->Unit) {
    var attempts by rememberSaveable { mutableIntStateOf(0) }
    var answer by rememberSaveable { mutableStateOf("") }
    val delivered = DemoInteractions.deliveries
    MainPage(modifier) {
        Eyebrow("Evaluaciones del curso")
        Heading("Tareas y exámenes.")
        MockNote()
        GroupPanel("Trabajo: Informe de investigación", "Grupal · 20 puntos · Hasta 2 intentos") {
            DemoBadge("Entrega abierta")
            Spacer(Modifier.height(10.dp))
            Text("Consigna: investigar una aplicación, documentar la metodología y adjuntar la evidencia.")
            Spacer(Modifier.height(10.dp))
            DemoNavRow("Ver rúbrica", "Fundamentación 8 · Método 6 · Presentación 6", Icons.Outlined.TableChart) { onOpen("rubric") }
            Spacer(Modifier.height(6.dp))
            DemoNavRow("Ver equipo asignado", "Líder e integrantes", Icons.Outlined.Groups) { onOpen("team") }
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(answer,{answer=it}, label={Text("Respuesta o enlace a entrega")}, modifier=Modifier.fillMaxWidth(),minLines=2)
            Text("Intentos: $attempts / 2",style=MaterialTheme.typography.labelSmall)
            ChemButton("Simular entrega",{if(attempts<2 && answer.isNotBlank()) { attempts++; delivered.add("Informe de investigación · intento $attempts") }},Modifier.fillMaxWidth())
            if(attempts>0) DemoBadge("Intento $attempts guardado en esta sesión")
        }
        GroupPanel("Examen: Fundamentos", "Individual · 3 intentos permitidos · DEMO") {
            DemoNavRow("Practicar evaluación", "Preguntas de opción múltiple", Icons.Outlined.Quiz) { onOpen("quiz") }
        }
        GroupPanel("Entregas de esta sesión", "Solo demostración") {
            if(delivered.isEmpty()) Text("Aún no hay intentos en esta sesión")
            delivered.forEach { Text("• $it") }
        }
    }
}

@Composable
fun RubricPage(modifier:Modifier=Modifier) {
    MainPage(modifier) {
        Eyebrow("Instrumento de calificación")
        Heading("Rúbrica y consigna.")
        listOf("Fundamentación" to "8 puntos", "Metodología" to "6 puntos", "Presentación" to "6 puntos").forEach { (criterion,score)->
            GroupPanel(criterion,"Niveles: Excelente / Logrado / En proceso / Inicial") { SimpleInfo("Puntaje máximo",score) }
        }
        GroupPanel("Consigna", "Archivo PDF opcional") { DemoBadge("Sin PDF asociado en el ejemplo") }
    }
}

@Composable
fun TeamPage(modifier:Modifier=Modifier,onOpen:(String)->Unit) {
    MainPage(modifier) {
        Eyebrow("Actividad grupal")
        Heading("Tu equipo.")
        GroupPanel("Grupo Alfa", "Equipo de ejemplo · Trabajo académico") {
            SimpleInfo("Líder", "Oscar Cantoral · DEMO")
            SimpleInfo("Integrante", "Lucía Torres · DEMO")
            SimpleInfo("Integrante", "Marco Ruiz · DEMO")
            SimpleInfo("Estado", "Trabajo pendiente")
        }
        DemoNavRow("Chat del equipo", "Conversación y coordinación",Icons.Outlined.Chat) { onOpen("messages") }
        DemoNavRow("Ver consignas e intentos", "Entregas grupales",Icons.Outlined.Assignment) { onOpen("assignments") }
    }
}

@Composable
fun LabsPage(modifier:Modifier=Modifier,onOpen:(String)->Unit) {
    MainPage(modifier) {
        Eyebrow("Experiencias temporales")
        Heading("Laboratorios virtuales.")
        GroupPanel("Simulación: Fuerzas de un ascensor", "2D · Docente habilitó 2 intentos · ejemplo") {
            DemoBadge("Habilitado")
            Spacer(Modifier.height(12.dp))
            ChemButton("Abrir simulador",{ onOpen("simulation") },Modifier.fillMaxWidth())
        }
        GroupPanel("Laboratorio de química", "Experimento con parámetros · DEMO") {
            DemoBadge("Programado")
            Text("Disponible cuando el docente active la ventana correspondiente.",style=MaterialTheme.typography.bodySmall)
        }
        GroupPanel("Visor 3D", "Observación del modelo sin manipulación espacial") {
            DemoNavRow("Abrir modelo 3D", "Recurso visual de ejemplo",Icons.Outlined.ViewInAr) { onOpen("model3d") }
        }
    }
}

@Composable
fun SimulationPage(modifier:Modifier=Modifier) {
    var mass by rememberSaveable { mutableFloatStateOf(70f) }
    var accel by rememberSaveable { mutableFloatStateOf(0f) }
    MainPage(modifier) {
        Eyebrow("Laboratorio · Simulación 2D")
        Heading("Ascensor y peso aparente.")
        GroupPanel("Parámetros ajustables", "Ejemplo didáctico local, sin servidor") {
            Text("Masa: ${mass.toInt()} kg")
            Slider(value=mass,onValueChange={mass=it},valueRange=30f..120f)
            Text("Aceleración: ${"%.1f".format(accel)} m/s² (+ sube)")
            Slider(value=accel,onValueChange={accel=it},valueRange=-3f..3f)
            Spacer(Modifier.height(10.dp))
            SimpleInfo("Peso (mg)", "${"%.1f".format(mass*9.81f)} N")
            SimpleInfo("Normal m(g+a)", "${"%.1f".format(mass*(9.81f+accel))} N")
            Text("Supone aceleración vertical uniforme y gravedad 9.81 m/s². No se utiliza ningún sensor del teléfono.",style=MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun InteractivePage(modifier:Modifier=Modifier) {
    var element by rememberSaveable { mutableStateOf("H") }
    var region by rememberSaveable { mutableStateOf("Lima") }
    MainPage(modifier) {
        Eyebrow("Contenido interactivo · 2D")
        Heading("Explora y descubre.")
        GroupPanel("Tabla periódica", "Interfaz nativa: toca un elemento para consultar su ficha") {
            Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(7.dp)) {
                listOf("H","He","Li","C","N","O").forEach { symbol ->
                    FilterChip(selected=symbol==element,onClick={element=symbol},label={Text(symbol)},shape=com.example.ch3mxr.core.ui.theme.ChemShapes.Component)
                }
            }
            val details=mapOf("H" to "Hidrógeno · número atómico 1", "He" to "Helio · número atómico 2", "Li" to "Litio · número atómico 3", "C" to "Carbono · número atómico 6", "N" to "Nitrógeno · número atómico 7", "O" to "Oxígeno · número atómico 8")
            Text(details[element].orEmpty(),fontWeight=FontWeight.Bold)
            Text("Aquí el recurso puede abrir su ficha, texto y materiales asociados. No es RA ni iframe.",style=MaterialTheme.typography.bodySmall)
        }
        GroupPanel("Mapa de regiones", "Selector nativo de ejemplo") {
            Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(7.dp)) {
                listOf("Lima","Cusco","Arequipa").forEach { name ->
                    FilterChip(selected=name==region,onClick={region=name},label={Text(name)},shape=com.example.ch3mxr.core.ui.theme.ChemShapes.Component)
                }
            }
            Text("Región seleccionada: $region")
            Text("En producción, el mapa gráfico podrá asociar zonas a contenidos mediante identificadores y eventos nativos.",style=MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun Model3DPage(modifier:Modifier=Modifier) {
    MainPage(modifier) {
        Eyebrow("Modelo tridimensional")
        Heading("Visualizador 3D.")
        GroupPanel("Molécula de agua", "Maqueta de la futura experiencia") {
            Icon(Icons.Outlined.ViewInAr,null,modifier=Modifier.size(88.dp),tint=MaterialTheme.colorScheme.primary)
            Text("El modelo real se renderizará cuando integremos el visor 3D. Esta pantalla no intenta simular interacción espacial.")
            SimpleInfo("Archivo", "Pendiente (.glb / .gltf)")
            SimpleInfo("Interacción", "Rotación de vista / información")
        }
    }
}

@Composable
fun QuizPage(modifier:Modifier=Modifier) {
    var selected by rememberSaveable { mutableStateOf("") }
    var checked by rememberSaveable { mutableStateOf(false) }
    var attempts by rememberSaveable { mutableIntStateOf(0) }
    MainPage(modifier) {
        Eyebrow("Evaluación individual · DEMO")
        Heading("Comprueba lo aprendido.")
        GroupPanel("Pregunta 1 de 1", "Tres intentos simulados") {
            Text("¿Qué mide el número atómico de un elemento?",fontWeight=FontWeight.Bold)
            listOf("Número de protones", "Masa del átomo", "Número de moléculas").forEach { answer ->
                Row(verticalAlignment=Alignment.CenterVertically) {
                    RadioButton(selected=selected==answer,onClick={selected=answer; checked=false})
                    Text(answer,modifier=Modifier.clickable { selected=answer;checked=false })
                }
            }
            ChemButton("Comprobar respuesta",{ if (selected.isNotEmpty() && attempts<3) {attempts++;checked=true} },Modifier.fillMaxWidth())
            if (checked) Text(if(selected=="Número de protones") "Correcto: el número atómico corresponde a los protones." else "Respuesta incorrecta; repasa la ficha y vuelve a intentar.")
            Text("Intentos: $attempts / 3",style=MaterialTheme.typography.labelSmall)
        }
    }
}
