package com.example.ch3mxr.core.model

data class CatalogResource(
  val id: String,
  val spaceId: String,
  val title: String,
  val kind: ResourceKind,
  val category: String,
  val author: String,
  val tags: String = "",
  val license: String = "Fuente externa",
  val url: String = "",
  val fileUri: String = "",
  val mime: String = "",
  val origin: String = "Biblioteca",
  val courseId: String = "",
)

data class SavedShortcut(
  val id: String,
  val spaceId: String,
  val title: String,
  val kind: String,
  val route: String,
)

data class CommunityPost(
  val id: String,
  val spaceId: String,
  val author: String,
  val initials: String,
  val text: String,
  val baseScore: Int = 0,
  val resourceId: String = "",
  val group: String = "Comunidad",
)

data class CommunityReply(
  val id: String,
  val postId: String,
  val author: String,
  val initials: String,
  val text: String,
  val baseScore: Int = 0,
  val resourceId: String = "",
)

data class ClassroomCourse(
  val id: String,
  val spaceId: String,
  val title: String,
  val sectionId: String,
  val sectionName: String,
  val teacher: String = "Docente del curso",
)

data class ClassroomEvent(
  val id: String,
  val courseId: String,
  val spaceId: String,
  val sectionId: String,
  val title: String,
  val type: String,
  val start: String,
  val end: String,
  val authorRole: AcademicRole,
)

data class ClassroomNote(
  val id: String,
  val courseId: String,
  val spaceId: String,
  val sectionId: String,
  val recordingId: String,
  val seconds: Int,
  val text: String,
  val photoUri: String,
  val authorRole: AcademicRole,
)

object ClassroomCatalog {
  val courses =
    listOf(
      ClassroomCourse(
        "utp_redes",
        "utp",
        "Redes y Comunicación de Datos II",
        "utp_redes_01",
        "Salón 01 · 2026-2",
        "José García",
      ),
      ClassroomCourse(
        "utp_mecanica",
        "utp",
        "Mecánica Clásica",
        "utp_mecanica_02",
        "Salón 02 · 2026-2",
        "Elena Rojas",
      ),
      ClassroomCourse(
        "utp_investigacion",
        "utp",
        "Formación para la Investigación",
        "utp_inv_01",
        "Salón 01 · 2026-2",
        "Mariana Salazar",
      ),
      ClassroomCourse(
        "senati_programacion",
        "senati",
        "Programación de aplicaciones",
        "senati_prog_a",
        "Grupo A · 2026-II",
        "Ricardo Ramos",
      ),
      ClassroomCourse(
        "senati_redes",
        "senati",
        "Fundamentos de redes",
        "senati_redes_b",
        "Grupo B · 2026-II",
        "Rosa Mendoza",
      ),
    )

  fun forSpace(spaceId: String) = courses.filter { it.spaceId == spaceId }

  fun find(id: String) = courses.firstOrNull { it.id == id }
}
