package com.example.ch3mxr.feature.experience

import androidx.compose.runtime.mutableStateListOf

/** Contenido de demostración. No son notas, matrículas, docentes ni pagos reales. */
data class DemoEvent(val spaceId: String, val day: Int, val time: String, val kind: String, val title: String)
data class DemoNotice(val spaceId: String, val title: String, val description: String, val type: String)
data class LibraryItem(val title: String, val category: String, val url: String, val tags: String, val description: String)

object ChemDemoData {
    val events = listOf(
        DemoEvent("personal", 24, "18:00", "Plan personal", "Repasar estructura atómica"),
        DemoEvent("personal", 25, "20:00", "Entrega", "Actividad de Química básica"),
        DemoEvent("utp", 24, "20:15", "Clase", "Redes y Comunicación de Datos II"),
        DemoEvent("utp", 25, "23:59", "Entrega", "Informe de investigación"),
        DemoEvent("utp", 27, "19:00", "Conferencia", "Seminario de ingeniería"),
        DemoEvent("senati", 24, "16:30", "Clase", "Programación de aplicaciones"),
        DemoEvent("senati", 26, "18:00", "Evaluación", "Práctica de redes")
    )
    val notices = listOf(
        DemoNotice("personal", "Continúa aprendiendo", "Tu lección de Química básica te espera.", "Curso"),
        DemoNotice("personal", "Nueva publicación", "Curiosos por la ciencia compartió una pregunta.", "Comunidad"),
        DemoNotice("utp", "Comunicado académico", "Revisa los documentos y cronogramas del periodo.", "Institucional"),
        DemoNotice("utp", "Entrega próxima", "Informe de investigación: 25 de octubre.", "Entrega"),
        DemoNotice("utp", "Cuota por revisar", "Consulta el estado de tu cuenta corriente.", "Pagos"),
        DemoNotice("senati", "Aviso de coordinación", "Consulta las novedades de tu programa.", "Institucional"),
        DemoNotice("senati", "Evaluación programada", "Práctica de redes: 26 de octubre.", "Evaluación")
    )
    val library = listOf(
        LibraryItem("Google Académico", "Buscador", "https://scholar.google.com/", "investigación, artículos", "Búsqueda de literatura científica."),
        LibraryItem("arXiv", "Repositorio", "https://arxiv.org/", "preprints, ciencias", "Prepublicaciones de investigación."),
        LibraryItem("IEEE Xplore", "Base de datos", "https://ieeexplore.ieee.org/", "ingeniería, informática", "Bibliografía de ingeniería y tecnología."),
        LibraryItem("PubMed", "Base de datos", "https://pubmed.ncbi.nlm.nih.gov/", "salud, medicina", "Artículos y citas biomédicas."),
        LibraryItem("Scopus", "Base de datos", "https://www.scopus.com/", "índices, investigación", "Índice bibliográfico; puede requerir suscripción."),
        LibraryItem("SciELO", "Repositorio", "https://scielo.org/", "acceso abierto, revistas", "Revistas científicas de acceso abierto."),
        LibraryItem("eLibro", "Biblioteca", "https://elibro.net/", "libros, ebooks", "Acceso sujeto a las licencias de la institución.")
    )
    val channels = listOf("Noticias ChemXR", "Seminarios y becas", "Comunicados institucionales")
}

/** Estado compartido entre pantallas durante la sesión DEMO. */
object DemoInteractions {
    val savedItems = mutableStateListOf<String>()
    val joinedCommunities = mutableStateListOf("Curiosos por la ciencia")
    val subscribedChannels = mutableStateListOf("Noticias ChemXR")
    val notes = mutableStateListOf("Resumen: enlaces químicos y estructura atómica.")
    val selectedEnrollments = mutableStateListOf<String>()
    val deliveries = mutableStateListOf<String>()
    val bookedResources = mutableStateListOf<String>()
    val likedPosts = mutableStateListOf<String>()
    val reports = mutableStateListOf<String>()
    val votedQuestions = mutableStateListOf<String>()
}
