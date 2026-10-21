package com.example.ch3mxr.feature.profile.model

/**
 * En producción este catálogo lo devolverá el servidor a partir de la cuenta autenticada.
 * Las instituciones no se crean automáticamente: deben venir de las asignaciones reales.
 */
data class StudySpace(
    val id: String,
    val name: String,
    val description: String,
    val academic: Boolean = false,
    val demo: Boolean = false
)

object DemoStudySpaces {
    // Datos EXCLUSIVAMENTE de maqueta. No representan una matrícula real.
    val available: List<StudySpace> = listOf(
        StudySpace("personal", "Mi espacio", "Aprendizaje personal"),
        StudySpace("demo-campus", "Campus de ejemplo", "Institución de demostración · sin afiliación real", academic = true, demo = true)
    )
}
