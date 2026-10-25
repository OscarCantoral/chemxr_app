package com.example.ch3mxr.feature.experience

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.components.*
import com.example.ch3mxr.feature.main.components.*
import com.example.ch3mxr.feature.profile.model.StudySpace

@Composable
fun LibraryPage(space: StudySpace, modifier:Modifier=Modifier, onOpen:(String)->Unit) {
    var search by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf("Todos") }
    val context = LocalContext.current
    MainPage(modifier) {
        SpaceContextHeader(space, "Biblioteca ${if(space.academic) "institucional" else "abierta"}")
        Heading("Investiga y consulta.")
        DemoNavRow("Mi estante personal", "Favoritos, apuntes y colecciones", Icons.Outlined.Bookmarks) { onOpen("shelf") }
        OutlinedTextField(value=search,onValueChange={search=it},label={Text("Buscar recursos, etiquetas o títulos")},modifier=Modifier.fillMaxWidth(),singleLine=true)
        Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
            listOf("Todos","Buscador","Repositorio","Base de datos").forEach { option ->
                FilterChip(selected=type==option,onClick={type=option},label={Text(option,style=MaterialTheme.typography.labelSmall)},shape=com.example.ch3mxr.core.ui.theme.ChemShapes.Component)
            }
        }
        ChemDemoData.library.filter { (type=="Todos" || it.category==type) && (search.isBlank() || (it.title+it.tags+it.description).contains(search,true)) }.forEach { item ->
            GroupPanel(item.title,"${item.category} · ${item.tags}") {
                Text(item.description,style=MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    ChemButton("Abrir sitio", {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.url)))
                    },Modifier.weight(1f))
                    ChemButton(if(item.title in DemoInteractions.savedItems) "Guardado" else "Guardar", {
                        if(item.title in DemoInteractions.savedItems) DemoInteractions.savedItems.remove(item.title)
                        else DemoInteractions.savedItems.add(item.title)
                    },Modifier.weight(1f),style=ChemButtonStyle.OUTLINED)
                }
            }
        }
        GroupPanel("Aportar enlace", "En una versión conectada, cada recurso será moderado y deberá respetar licencias de uso") {
            DemoNavRow("Proponer nuevo recurso", "Compartir un enlace legal al catálogo", Icons.Outlined.AddLink) { onOpen("submitResource") }
        }
    }
}

@Composable
fun ShelfPage(modifier:Modifier=Modifier,onOpen:(String)->Unit) {
    MainPage(modifier) {
        Eyebrow("Colección personal")
        Heading("Mi estante.")
        DemoNavRow("Mis apuntes", "Notas de texto y referencias", Icons.Outlined.EditNote) {onOpen("notes")}
        SectionHeading("Enlaces guardados")
        if(DemoInteractions.savedItems.isEmpty()) Text("Guarda recursos desde la Biblioteca del espacio para encontrarlos aquí.")
        DemoInteractions.savedItems.toList().forEach { name ->
            GroupPanel(name,"Colección · Favoritos") {
                ChemButton("Quitar de guardados",{DemoInteractions.savedItems.remove(name)},Modifier.fillMaxWidth(),style=ChemButtonStyle.OUTLINED)
            }
        }
        SectionHeading("Cursos guardados")
        DemoNavRow("Química básica", "Curso de ejemplo",Icons.Outlined.MenuBook) {onOpen("course")}
    }
}

@Composable
fun SubmitResourcePage(modifier:Modifier=Modifier) {
    var title by rememberSaveable { mutableStateOf("") }
    var link by rememberSaveable { mutableStateOf("") }
    var sent by rememberSaveable { mutableStateOf(false) }
    MainPage(modifier) {
        Eyebrow("Biblioteca colaborativa")
        Heading("Comparte una referencia.")
        GroupPanel("Propuesta de enlace", "El enlace se revisará antes de figurar en un catálogo real") {
            OutlinedTextField(value=title,onValueChange={title=it;sent=false},label={Text("Título")},modifier=Modifier.fillMaxWidth())
            OutlinedTextField(value=link,onValueChange={link=it;sent=false},label={Text("URL de origen")},modifier=Modifier.fillMaxWidth())
            ChemButton("Enviar propuesta DEMO",{if(title.isNotBlank() && link.startsWith("https://")) sent=true},Modifier.fillMaxWidth())
            if(sent) DemoBadge("Propuesta simulada · sin envío a servidor")
            Text("No subas libros protegidos sin autorización. Comparte enlaces legítimos y respeta las licencias.",style=MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun CommunityHubPage(space:StudySpace,modifier:Modifier=Modifier,onOpen:(String)->Unit) {
    MainPage(modifier) {
        SpaceContextHeader(space,"Comunidad y colaboración")
        Heading("Aprender juntos.")
        DemoNavRow("Comunidades", "Grupos públicos, privados, membresías y reglas",Icons.Outlined.Groups) {onOpen("communityList")}
        DemoNavRow("Canales", "Difusión oficial y suscriptores",Icons.Outlined.Campaign) {onOpen("channels")}
        DemoNavRow("Mensajes", "Conversaciones con docentes y grupos",Icons.Outlined.Chat) {onOpen("messages")}
        DemoNavRow("Preguntas académicas", "Debates evaluables solo dentro del curso",Icons.Outlined.QuestionAnswer) {onOpen("forum")}
    }
}

@Composable
fun CommunitiesPage(space:StudySpace,modifier:Modifier=Modifier,onOpen:(String)->Unit) {
    val joined=DemoInteractions.joinedCommunities
    val examples = if(space.academic) listOf(
        "Comunidad ${space.name}" to "Pública dentro del campus · DEMO",
        "Proyectos ${space.name}" to "Privada · Solicitar ingreso"
    ) else listOf(
        "Curiosos por la ciencia" to "Pública · 342 miembros",
        "Programamos juntos" to "Privada · Solicitar ingreso",
        "Club de investigadores" to "Pública · 83 miembros"
    )
    MainPage(modifier) {
        SpaceContextHeader(space,"Comunidades visibles, con permisos de acceso")
        Heading("Explora comunidades.")
        examples.forEach { (name,subtitle)->
            GroupPanel(name,subtitle) {
                SimpleInfo("Estado",if(name in joined) "Miembro · DEMO" else "No unido")
                ChemButton(if(name in joined) "Salir de comunidad" else if(subtitle.startsWith("Privada")) "Solicitar unirse" else "Unirme",{
                    if(name in joined) joined.remove(name) else joined.add(name)
                },Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                DemoNavRow("Ver publicaciones", "Reglas, moderación y recursos", Icons.Outlined.Feed) {onOpen("communityDetail")}
            }
        }
        GroupPanel("Política contra abandono", "No se modifica automáticamente la privacidad de una comunidad") {
            Text("La inactividad podría ocasionar un aviso y archivado según las reglas del servidor. Las comunidades privadas nunca se vuelven públicas automáticamente.",style=MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun CommunityDetailPage(space:StudySpace,modifier:Modifier=Modifier,onOpen:(String)->Unit) {
    var newPost by rememberSaveable { mutableStateOf("") }
    var posted by rememberSaveable { mutableStateOf("") }
    val liked=DemoInteractions.likedPosts
    MainPage(modifier) {
        Eyebrow("Comunidad · DEMO")
        Heading(if(space.academic) "Comunidad ${space.name}." else "Curiosos por la ciencia.")
        GroupPanel("Información de la comunidad", "Pública · 342 miembros · 3 moderadores") {
            SimpleInfo("Regla", "Respeto y fuentes verificables")
            SimpleInfo("Bots", "Identificados como automatizados")
            SimpleInfo("Privacidad", "Pública")
        }
        GroupPanel("Publicar", "Comparte un tema, material, imagen o enlace") {
            OutlinedTextField(newPost,{newPost=it},label={Text("¿Qué quieres compartir?")},modifier=Modifier.fillMaxWidth(),minLines=3)
            ChemButton("Publicar en DEMO", {if(newPost.isNotBlank()){posted=newPost;newPost=""}},Modifier.fillMaxWidth())
        }
        if(posted.isNotBlank()) GroupPanel("Tú · Ahora", "Publicación creada solo en el prototipo") { Text(posted) }
        GroupPanel("Lucía Torres", "Hace 2 horas · Post académico") {
            Text("¿Qué sensores usarían para un robot que detecta obstáculos?")
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                ChemButton(if("post1" in liked) "Te gusta" else "Me gusta",{
                    if("post1" in liked) liked.remove("post1") else liked.add("post1")
                },Modifier.weight(1f),style=ChemButtonStyle.OUTLINED)
                ChemButton("Denunciar",{DemoInteractions.reports.add("Post de demostración")},Modifier.weight(1f),style=ChemButtonStyle.OUTLINED)
            }
        }
        GroupPanel("Bot de bienvenida · Automatizado", "Mensaje identificado como generado por el sistema") {
            Text("Bienvenidos. Recuerden citar sus fuentes y consultar las reglas de esta comunidad.")
        }
        DemoNavRow("Ver canales de difusión", "Solo autores autorizados publican", Icons.Outlined.Campaign) {onOpen("channels")}
    }
}

@Composable
fun ChannelsPage(space:StudySpace,modifier:Modifier=Modifier) {
    val subscribed=DemoInteractions.subscribedChannels
    val channels=if(space.academic) listOf("Comunicados ${space.name}","Eventos ${space.name}","Biblioteca ${space.name}") else ChemDemoData.channels
    MainPage(modifier) {
        Eyebrow("Difusión · Solo lectura · ${space.name}")
        Heading("Canales.")
        channels.forEach { name ->
            GroupPanel(name,"Anuncios unidireccionales · DEMO") {
                SimpleInfo("Seguimiento",if(name in subscribed) "Suscrito" else "No suscrito")
                Text("Comunicados, actividades y recursos publicados por autores del canal.",style=MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(8.dp))
                ChemButton(if(name in subscribed) "Dejar de seguir" else "Seguir canal",{
                    if(name in subscribed) subscribed.remove(name) else subscribed.add(name)
                },Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun MessagesPage(modifier:Modifier=Modifier) {
    var selected by rememberSaveable { mutableStateOf("Docente · Ana Morales") }
    var message by rememberSaveable { mutableStateOf("") }
    var lastSent by rememberSaveable { mutableStateOf("") }
    MainPage(modifier) {
        Eyebrow("Mensajería académica")
        Heading("Conversaciones.")
        Row(horizontalArrangement=Arrangement.spacedBy(7.dp)) {
            listOf("Docente · Ana Morales","Grupo Alfa").forEach { title ->
                FilterChip(selected=selected==title,onClick={selected=title;lastSent=""},label={Text(title,style=MaterialTheme.typography.labelSmall)},shape=com.example.ch3mxr.core.ui.theme.ChemShapes.Component)
            }
        }
        GroupPanel(selected,"Chat simulado") {
            Text(if(selected.startsWith("Docente")) "Hola, ¿en qué puedo ayudarte con el curso?" else "Equipo: coordinemos la entrega del proyecto.")
            if(lastSent.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                DemoBadge("Tú: $lastSent")
            }
            OutlinedTextField(message,{message=it},label={Text("Escribe un mensaje")},modifier=Modifier.fillMaxWidth())
            ChemButton("Enviar DEMO",{if(message.isNotBlank()){lastSent=message;message=""}},Modifier.fillMaxWidth())
        }
        MockNote()
    }
}

@Composable
fun ForumPage(modifier:Modifier=Modifier) {
    var answer by rememberSaveable { mutableStateOf("") }
    var posted by rememberSaveable { mutableStateOf("") }
    val votes=DemoInteractions.votedQuestions
    MainPage(modifier) {
        Eyebrow("Foro exclusivo del curso")
        Heading("Preguntas y debates.")
        GroupPanel("¿Cuándo utilizar OSPF?", "Pregunta académica · 3 respuestas") {
            Text("Respuesta aceptada (DEMO): cuando se necesita un protocolo dinámico de enrutamiento interno.")
            Spacer(Modifier.height(10.dp))
            ChemButton(if("ospf" in votes) "Voto registrado" else "Votar respuesta útil", {
                if("ospf" !in votes) votes.add("ospf") else votes.remove("ospf")
            },Modifier.fillMaxWidth(),style=ChemButtonStyle.OUTLINED)
        }
        GroupPanel("Participa en el debate", "Las respuestas son diferentes de las reacciones sociales") {
            OutlinedTextField(answer,{answer=it},label={Text("Tu respuesta")},minLines=3,modifier=Modifier.fillMaxWidth())
            ChemButton("Responder DEMO",{if(answer.isNotBlank()){posted=answer;answer=""}},Modifier.fillMaxWidth())
            if(posted.isNotBlank()) DemoBadge("Respuesta de prueba: $posted")
        }
        GroupPanel("Asistente de foro · BOT", "Recordatorio automático identificado como bot") {
            Text("Revisen la consigna y publiquen sus fuentes antes de responder.")
        }
        GroupPanel("Moderación", "Contenido ofensivo, spam y respuestas no académicas") {
            ChemButton("Denunciar publicación",{DemoInteractions.reports.add("Foro del curso")},Modifier.fillMaxWidth(),style=ChemButtonStyle.OUTLINED)
        }
    }
}

@Composable
fun TeacherPage(modifier:Modifier=Modifier,onOpen:(String)->Unit) {
    MainPage(modifier) {
        Eyebrow("Perfil público del docente · DEMO")
        Heading("Ana Morales.")
        GroupPanel("Docente de Química", "Especialidad y trayectoria") {
            SimpleInfo("Área", "Ciencias naturales")
            SimpleInfo("Cursos", "Química básica")
            SimpleInfo("Insignias", "Educación y divulgación")
            Text("Este perfil de demostración no corresponde a un docente real.",style=MaterialTheme.typography.labelSmall)
        }
        DemoNavRow("Contactar al docente", "Mensajería académica",Icons.Outlined.Chat) {onOpen("messages")}
        DemoNavRow("Reseñas del curso", "Valoraciones de aprendizaje",Icons.Outlined.StarOutline) {onOpen("reviews")}
    }
}

@Composable
fun ReviewsPage(modifier:Modifier=Modifier) {
    var rating by rememberSaveable { mutableIntStateOf(0) }
    var comment by rememberSaveable { mutableStateOf("") }
    var submitted by rememberSaveable { mutableStateOf(false) }
    MainPage(modifier) {
        Eyebrow("Valoraciones")
        Heading("Reseña de curso.")
        GroupPanel("Tu experiencia", "La evaluación institucional anónima del docente es independiente de esta reseña") {
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                (1..5).forEach { n -> FilterChip(selected=rating==n,onClick={rating=n;submitted=false},label={Text(n.toString())},shape=com.example.ch3mxr.core.ui.theme.ChemShapes.Component) }
            }
            OutlinedTextField(comment,{comment=it;submitted=false},modifier=Modifier.fillMaxWidth(),label={Text("Comparte tu opinión")},minLines=3)
            ChemButton("Publicar reseña DEMO",{if(rating>0 && comment.isNotBlank()) submitted=true},Modifier.fillMaxWidth())
            if(submitted) DemoBadge("Reseña guardada en maqueta")
        }
    }
}

@Composable
fun AchievementsPage(modifier:Modifier=Modifier) {
    val context=LocalContext.current
    MainPage(modifier) {
        Eyebrow("Credenciales y logros")
        Heading("Cada paso cuenta.")
        listOf("Primer curso completado", "Explorador de ciencias", "Participación en comunidad").forEach { name ->
            GroupPanel(name,"Insignia ilustrativa · pendiente de emitir") {
                DemoBadge("DEMO · Sin certificación")
                Spacer(Modifier.height(8.dp))
                ChemButton("Compartir texto",{
                    val intent=Intent(Intent.ACTION_SEND).apply {type="text/plain";putExtra(Intent.EXTRA_TEXT,"ChemXR: $name (demostración, sin acreditación oficial)")}
                    context.startActivity(Intent.createChooser(intent,"Compartir logro"))
                },Modifier.fillMaxWidth(),style=ChemButtonStyle.OUTLINED)
            }
        }
        Text("La publicación real en LinkedIn requiere una credencial verificable y la autorización del usuario.",style=MaterialTheme.typography.labelSmall)
    }
}
