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
    val periods: List<String> = emptyList()
)

object DemoStudySpaces {
    val available: List<StudySpace> = listOf(
        StudySpace("personal", "Mi espacio", "Aprendizaje libre y personal"),
        StudySpace(
            "utp", "UTP", "Universidad Tecnológica del Perú · DEMO",
            academic = true, demo = true, primary = Color(0xFFB50D30), secondary = Color.Black,
            iconLight = R.drawable.ic_utp_light, iconDark = R.drawable.ic_utp_dark,
            studentCode = "U-DEMO-001", program = "Ingeniería de Sistemas", campus = "Lima Sur",
            periods = listOf("2026 - Ciclo 2", "2026 - Ciclo 1", "2025 - Ciclo 2")
        ),
        StudySpace(
            "senati", "Senati", "Institución tecnológica · DEMO",
            academic = true, demo = true, primary = Color(0xFF0A39E4), secondary = Color.White,
            iconLight = R.drawable.ic_senati_light, iconDark = R.drawable.ic_senati_dark,
            studentCode = "S-DEMO-002", program = "Desarrollo de Software", campus = "Lima",
            periods = listOf("2026 - Periodo II", "2026 - Periodo I")
        )
    )
}
