package com.example.ch3mxr.feature.experience

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.feature.main.components.*
import com.example.ch3mxr.feature.profile.model.StudySpace

@Composable
fun InstitutionHomeScreen(space: StudySpace, period: String, modifier: Modifier = Modifier, onOpen: (String)->Unit) {
    MainLazyPage(modifier) {
        item { SpaceContextHeader(space, "Tu campus académico · $period") }
        item { Heading("Bienvenido a ${space.name}.", 26) }
        item {
            GroupPanel("Tu información académica", "Credenciales y asignaciones de ejemplo") {
                SimpleInfo("Carrera / programa", space.program)
                SimpleInfo("Código de estudiante", space.studentCode)
                SimpleInfo("Campus", space.campus)
                SimpleInfo("Periodo", period)
            }
        }
        item { SectionHeading("Tu campus") }
        item { DemoNavRow("Matrícula y cursos", "Oferta, créditos y horarios", Icons.Outlined.AppRegistration) { onOpen("enrollment") } }
        item { DemoNavRow("Mi récord y progreso", "Notas, créditos, malla y asistencia", Icons.Outlined.BarChart) { onOpen("record") } }
        item { DemoNavRow("Cuenta corriente", "Cuotas, comprobantes y descuentos", Icons.Outlined.Payments) { onOpen("finance") } }
        item { DemoNavRow("Documentos oficiales", "Reglamentos, ética y guías", Icons.Outlined.Description) { onOpen("documents") } }
        item { DemoNavRow("Trámites y solicitudes", "Seguimiento de expedientes", Icons.Outlined.FactCheck) { onOpen("procedures") } }
        item { DemoNavRow("Servicios y reservas", "Biblioteca física, laboratorios y cubículos", Icons.Outlined.MeetingRoom) { onOpen("services") } }
        item { DemoNavRow("Biblioteca institucional", "Catálogo y repositorios", Icons.Outlined.LocalLibrary) { onOpen("library") } }
        item { DemoNavRow("Sesiones virtuales", "Meet · Teams · Zoom · Grabaciones", Icons.Outlined.VideoCall) { onOpen("sessions") } }
        item { DemoNavRow("Canales y comunicados", "Avisos oficiales de tu institución", Icons.Outlined.Campaign) { onOpen("channels") } }
        item { DemoNavRow("Encuestas", "Opinión sobre cursos y docentes", Icons.Outlined.RateReview) { onOpen("surveys") } }
        item { DemoNavRow("Bolsa de trabajo", "Oportunidades y empleabilidad", Icons.Outlined.WorkOutline) { onOpen("jobs") } }
        item { DemoNavRow("Soporte institucional", "Tickets y atención", Icons.Outlined.SupportAgent) { onOpen("support") } }
        item { MockNote() }
    }
}
