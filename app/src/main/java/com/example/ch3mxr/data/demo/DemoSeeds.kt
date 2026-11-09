package com.example.ch3mxr.data.demo

import com.example.ch3mxr.core.model.*

private data class Reference(
  val title: String,
  val category: String,
  val url: String,
  val tags: String,
  val description: String,
)

private object DemoReferences {
  val library =
    listOf(
      Reference(
        "Google Académico",
        "Buscador",
        "https://scholar.google.com/",
        "investigación, artículos",
        "Búsqueda de literatura científica.",
      ),
      Reference(
        "arXiv",
        "Repositorio",
        "https://arxiv.org/",
        "preprints, ciencias",
        "Prepublicaciones de investigación.",
      ),
      Reference(
        "IEEE Xplore",
        "Base de datos",
        "https://ieeexplore.ieee.org/",
        "ingeniería, informática",
        "Bibliografía de ingeniería y tecnología.",
      ),
      Reference(
        "PubMed",
        "Base de datos",
        "https://pubmed.ncbi.nlm.nih.gov/",
        "salud, medicina",
        "Artículos y citas biomédicas.",
      ),
      Reference(
        "Scopus",
        "Base de datos",
        "https://www.scopus.com/",
        "índices, investigación",
        "Índice bibliográfico; puede requerir suscripción.",
      ),
      Reference(
        "SciELO",
        "Repositorio",
        "https://scielo.org/",
        "acceso abierto, revistas",
        "Revistas científicas de acceso abierto.",
      ),
      Reference(
        "eLibro",
        "Biblioteca",
        "https://elibro.net/",
        "libros, ebooks",
        "Acceso sujeto a las licencias de la institución.",
      ),
    )
}

fun seedResources(): List<CatalogResource> =
  listOf("personal", "utp", "senati").flatMap { space ->
    DemoReferences.library.mapIndexed { i, x ->
      CatalogResource(
        "${space}_reference_$i",
        space,
        x.title,
        ResourceKind.LINK,
        x.category,
        "Fuente externa",
        tags = x.tags,
        url = x.url,
      )
    }
  }

fun seedPosts() =
  listOf(
    CommunityPost(
      "lucia",
      "personal",
      "Lucía Torres",
      "LT",
      "¿Qué sensores usarían para un robot que detecta obstáculos? Estoy preparando mi primer proyecto.",
      12,
      group = "Curiosos por la ciencia",
    ),
    CommunityPost(
      "mateo",
      "personal",
      "Mateo Ramos",
      "MR",
      "Hoy hice mi primer modelo 3D de una molécula. ¿Qué recurso les ha servido para estudiar química?",
      8,
      group = "Programamos juntos",
    ),
    CommunityPost(
      "utp_post1",
      "utp",
      "Lucía Torres",
      "LT",
      "¿Organizamos una sesión para resolver los ejercicios de Mecánica Clásica?",
      6,
      group = "Comunidad UTP",
    ),
    CommunityPost(
      "utp_post2",
      "utp",
      "Mateo Ramos",
      "MR",
      "Comparto una referencia para preparar el informe de investigación.",
      4,
      "utp_reference_0",
      "Comunidad UTP",
    ),
    CommunityPost(
      "senati_post1",
      "senati",
      "Ana Beltrán",
      "AB",
      "¿Qué práctica recomiendan para entender las redes locales?",
      5,
      group = "Comunidad SENATI",
    ),
    CommunityPost(
      "senati_post2",
      "senati",
      "Diego Cruz",
      "DC",
      "Dejé una fuente de ingeniería en la biblioteca del espacio.",
      3,
      "senati_reference_2",
      "Comunidad SENATI",
    ),
  )

fun seedReplies() =
  seedPosts().flatMap { post ->
    listOf(
      CommunityReply(
        "${post.id}_c1",
        post.id,
        "Daniel Soto",
        "DS",
        if (post.id == "lucia")
          "Empezaría con un sensor ultrasónico; mide distintas distancias y registra los resultados."
        else "Podemos revisar los ejemplos y comparar nuestros resultados.",
        4,
      ),
      CommunityReply(
        "${post.id}_c2",
        post.id,
        "Ana Beltrán",
        "AB",
        "Me sirve organizar las ideas y citar la fuente de cada recurso.",
        2,
      ),
      CommunityReply(
        "${post.id}_c3",
        post.id,
        "Carlos Medina",
        "CM",
        "¡Comparte tus avances para que podamos ayudarte!",
        1,
      ),
    )
  }

fun bundledResources(): List<CatalogResource> =
  listOf(
    CatalogResource(
      "manual_quimica",
      "personal",
      "Manual de química",
      ResourceKind.PDF,
      "Química",
      "Material recuperado",
      fileUri = "asset:///manuals/manual_quimica.pdf",
      mime = "application/pdf",
      courseId = "adrom_chemistry",
    ),
    CatalogResource(
      "manual_biologia",
      "personal",
      "Manual de biología",
      ResourceKind.PDF,
      "Biología",
      "Material recuperado",
      fileUri = "asset:///manuals/manual_biologia.pdf",
      mime = "application/pdf",
    ),
    CatalogResource(
      "manual_historia",
      "personal",
      "Manual de historia",
      ResourceKind.PDF,
      "Historia",
      "Material recuperado",
      fileUri = "asset:///manuals/manual_historia.pdf",
      mime = "application/pdf",
    ),
    CatalogResource(
      "manual_matematicas",
      "personal",
      "Manual de matemáticas",
      ResourceKind.PDF,
      "Matemáticas",
      "Material recuperado",
      fileUri = "asset:///manuals/manual_matematicas.pdf",
      mime = "application/pdf",
    ),
    CatalogResource(
      "quimica_video",
      "personal",
      "Introducción a la química · Video",
      ResourceKind.VIDEO,
      "Química",
      "Fuente del proyecto anterior",
      url = "https://www.youtube.com/watch?v=oPrXErLylxA",
      courseId = "adrom_chemistry",
    ),
    CatalogResource(
      "atom_model",
      "personal",
      "Átomo · Esquema didáctico 3D",
      ResourceKind.MODEL_3D,
      "Química",
      "Equipo ADROM",
      license = "Generado para este proyecto",
      fileUri = "asset:///models/atom.glb",
      mime = "model/gltf-binary",
      courseId = "adrom_chemistry",
    ),
  )
