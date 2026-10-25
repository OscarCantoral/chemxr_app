package com.example.ch3mxr.feature.profile.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.ch3mxr.R
import com.example.ch3mxr.core.ui.theme.ChemLime

/**
 * Contrato de UI para un espacio asignado a la identidad autenticada.
 * La maqueta utiliza datos locales; en producción el backend decidirá pertenencia,
 * permisos, colores e imágenes. Ningún campo demo otorga acceso institucional real.
 */
data class StudySpace(
    val id: String,
    val name: String,
    val description: String,
    val academic: Boolean = false,
    val demo: Boolean = false,
    val primary: Color = ChemLime,
    val secondary: Color = Color(0xFF121C26),
    @DrawableRes val iconLight: Int? = null,
    @DrawableRes val iconDark: Int? = null,
    val studentCode: String = "",
    val program: String = "",
    val campus: String = "",
    val modality: String = "Presencial",
    val institutionalEmail: String = "",
    val periods: List<String> = emptyList()
)

object DemoStudySpaces {
    val available: List<StudySpace> = listOf(
        StudySpace("personal", "Mi espacio", "Aprendizaje libre y personal"),
        StudySpace(
            "utp", "UTP", "Universidad Tecnológica del Perú · DEMO",
            academic = true, demo = true, primary = Color(0xFFB50D30), secondary = Color.Black,
            iconLight = R.drawable.logoutp_2, iconDark = R.drawable.logoutp,
            studentCode = "U-DEMO-001", program = "Ingeniería de Sistemas", campus = "Lima Sur",
            modality = "A distancia · 80 % virtual", institutionalEmail = "alumno@utp.edu.pe",
            periods = listOf("2026 - Ciclo 2", "2026 - Ciclo 1", "2025 - Ciclo 2")
        ),
        StudySpace(
            "senati", "Senati", "Institución tecnológica · DEMO",
            academic = true, demo = true, primary = Color(0xFF0A39E4), secondary = Color.White,
            iconLight = R.drawable.logosena_2, iconDark = R.drawable.logosena,
            studentCode = "S-DEMO-002", program = "Desarrollo de Software", campus = "Lima",
            modality = "Presencial", institutionalEmail = "alumno@senati.pe",
            periods = listOf("2026 - Periodo II", "2026 - Periodo I")
        )
    )
}
