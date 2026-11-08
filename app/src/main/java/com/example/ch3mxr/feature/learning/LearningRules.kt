package com.example.ch3mxr.feature.learning

enum class StudentPlan { FREE, PRO }
enum class CourseOwner { ADROM, TEACHER }
enum class CourseAccess { FREE, PRO, PURCHASE }
enum class AcademicRole(val label: String) { STUDENT("Estudiante"), DELEGATE("Delegado"), TEACHER("Docente") }
enum class ResourceKind(val label: String) {
    PDF("Libro / PDF"), VIDEO("Vídeo"), AUDIO("Audio"), IMAGE("Imagen"), MODEL_3D("Modelo 3D / XR"),
    DOCUMENT("Documento"), SLIDES("Presentación"), SHEET("Hoja de cálculo"), LINK("Enlace"), OTHER("Otro archivo")
}

/** Reglas de producto, sin dependencias Android: el servidor deberá repetirlas al integrar la API. */
object LearningRules {
    fun canStudy(access: CourseAccess, plan: StudentPlan, purchased: Boolean): Boolean = when (access) {
        CourseAccess.FREE -> true
        CourseAccess.PRO -> plan == StudentPlan.PRO
        CourseAccess.PURCHASE -> purchased
    }
    fun canPublishClass(role: AcademicRole): Boolean = role == AcademicRole.DELEGATE || role == AcademicRole.TEACHER
    fun webLinks(text: String): List<String> = Regex("https?://[^\\s]+",RegexOption.IGNORE_CASE).findAll(text)
        .map {it.value.trimEnd('.',',',')',']',';')}.distinct().toList()
    fun nextVote(current: Int, requested: Int): Int = if (current == requested) 0 else requested.coerceIn(-1, 1)
    fun parseTimestamp(value: String): Int? {
        val parts = value.trim().split(':')
        if (parts.size !in 2..3 || parts.any { it.isEmpty() || it.any { c -> !c.isDigit() } }) return null
        val n = parts.map { it.toIntOrNull() ?: return null }
        if (n.last() !in 0..59 || (n.size == 3 && n[1] !in 0..59)) return null
        val total = if (n.size == 2) n[0].toLong() * 60 + n[1] else n[0].toLong() * 3600 + n[1] * 60 + n[2]
        return total.takeIf { it <= Int.MAX_VALUE }?.toInt()
    }
    fun formatTimestamp(seconds: Int): String = "${seconds / 60}:${(seconds % 60).toString().padStart(2, '0')}"
}
