package com.example.ch3mxr.ui.model

import com.example.ch3mxr.R

data class CourseModule(
    val number: String,
    val title: String,
    val subtitle: String
)

data class Course(
    val id: String,
    val title: String,
    val tagline: String,
    val description: String,
    val imageRes: Int,
    val manualAsset: String,
    val videoUrl: String? = null,
    val modules: List<CourseModule>
)

object CourseCatalog {

    val quimica = Course(
        id = "quimica",
        title = "QUÍMICA",
        tagline = "Explora los módulos del curso",
        description = "Átomos, moléculas y enlaces",
        imageRes = R.drawable.fond_curs_quim,
        manualAsset = "manuals/manual_quimica.pdf",
        videoUrl = "https://www.youtube.com/watch?v=oPrXErLylxA",
        modules = listOf(
            CourseModule("01", "ÁTOMOS", "Estructura\nde la materia"),
            CourseModule("02", "ENLACES", "Iónicos"),
            CourseModule("03", "MINERALES", "Composición\ny propiedades"),
            CourseModule("04", "OTROS", "Más contenido")
        )
    )

    val biologia = Course(
        id = "biologia",
        title = "BIOLOGÍA",
        tagline = "Explora los módulos del curso",
        description = "Células, tejidos y genética",
        imageRes = R.drawable.fond_curs_biol,
        manualAsset = "manuals/manual_biologia.pdf",
        modules = listOf(
            CourseModule("01", "CÉLULA", "Estructura\ny función"),
            CourseModule("02", "GENÉTICA", "ADN y herencia"),
            CourseModule("03", "TEJIDOS", "Tipos y funciones"),
            CourseModule("04", "ECOSISTEMAS", "Más contenido")
        )
    )

    val matematicas = Course(
        id = "matematicas",
        title = "MATEMÁTICAS",
        tagline = "Explora los módulos del curso",
        description = "Álgebra, geometría y cálculo",
        imageRes = R.drawable.fond_curs_mate,
        manualAsset = "manuals/manual_matematicas.pdf",
        modules = listOf(
            CourseModule("01", "ÁLGEBRA", "Ecuaciones\ny expresiones"),
            CourseModule("02", "GEOMETRÍA", "Figuras y teoremas"),
            CourseModule("03", "TRIGONOMETRÍA", "Razones\ntrigonométricas"),
            CourseModule("04", "CÁLCULO", "Límites y derivadas")
        )
    )

    val historia = Course(
        id = "historia",
        title = "HISTORIA",
        tagline = "Explora los módulos del curso",
        description = "Historia Universal",
        imageRes = R.drawable.fond_curs_hist,
        manualAsset = "manuals/manual_historia.pdf",
        modules = listOf(
            CourseModule("01", "ANTIGÜEDAD", "Primeras\ncivilizaciones"),
            CourseModule("02", "MEDIEVO", "Feudalismo\ny imperios"),
            CourseModule("03", "MODERNA", "Revoluciones"),
            CourseModule("04", "CONTEMPORÁNEA", "Siglos XX y XXI")
        )
    )

    val all = listOf(quimica, biologia, matematicas, historia)

    fun byId(id: String): Course =
        all.first { it.id == id }
}
