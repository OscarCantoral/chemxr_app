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
fun LibraryPage(space:StudySpace,modifier:Modifier=Modifier,onOpen:(String)->Unit) {
    com.example.ch3mxr.feature.library.catalog.ResourceLibraryPage(space,modifier,onOpen)
}

@Composable
fun ShelfPage(modifier:Modifier=Modifier,onOpen:(String)->Unit) {
    com.example.ch3mxr.feature.library.catalog.PersonalShelfPage(modifier,onOpen)
}

@Composable
fun SubmitResourcePage(space:StudySpace,modifier:Modifier=Modifier,onOpen:(String)->Unit) {
    com.example.ch3mxr.feature.library.catalog.RegisterResourcePage(space,modifier,onOpen)
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
    com.example.ch3mxr.feature.community.CommunityFeedScreen(space,modifier,onOpen)
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
    MainPage(modifier) {
        Eyebrow("Foro exclusivo del curso")
        Heading("Preguntas y debates.")
        GroupPanel("¿Cuándo utilizar OSPF?", "Pregunta académica · 3 respuestas") {
            Text("Respuesta aceptada (DEMO): cuando se necesita un protocolo dinámico de enrutamiento interno.")
            Spacer(Modifier.height(10.dp))
            com.example.ch3mxr.feature.community.VoteControls("forum:ospf",8)

        }
        GroupPanel("Participa en el debate", "Comparte una explicación con fuentes") {
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
