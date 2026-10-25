package com.example.ch3mxr.feature.experience

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.components.ChemButton
import com.example.ch3mxr.feature.main.components.*
import java.time.LocalDate

/** Formulario DEMO para aulas temporales privadas, sin reservas ni cursos reales. */
@Composable
fun PrivateClassPage(modifier:Modifier=Modifier) {
    var title by rememberSaveable {mutableStateOf("")}
    var topic by rememberSaveable {mutableStateOf("")}
    var description by rememberSaveable {mutableStateOf("")}
    var expires by rememberSaveable {mutableStateOf("")}
    var publicContribution by rememberSaveable {mutableStateOf(false)}
    var created by rememberSaveable {mutableStateOf(false)}
    val validDate=runCatching {LocalDate.parse(expires).isAfter(LocalDate.now())}.getOrDefault(false)
    MainPage(modifier) {
        Eyebrow("Mi espacio · Herramientas gratuitas")
        Heading("Crear aula temporal.")
        MockNote()
        GroupPanel("Tu aula", "Las aulas tienen módulos, participantes, recursos y actividades como cualquier curso") {
            OutlinedTextField(title,{title=it;created=false},label={Text("Nombre del aula")},modifier=Modifier.fillMaxWidth())
            OutlinedTextField(topic,{topic=it;created=false},label={Text("Tema")},modifier=Modifier.fillMaxWidth())
            OutlinedTextField(description,{description=it;created=false},label={Text("Descripción")},modifier=Modifier.fillMaxWidth(),minLines=2)
            OutlinedTextField(expires,{expires=it;created=false},label={Text("Caducidad (AAAA-MM-DD)")},modifier=Modifier.fillMaxWidth(),singleLine=true)
            Text("La vigencia y capacidad máximas serán verificadas por el plan real. Este formulario no crea un aula en el servidor.",style=MaterialTheme.typography.labelSmall)
        }
        GroupPanel("Destino del contenido", "Las conversaciones y entregas privadas nunca se publican automáticamente") {
            Row(verticalAlignment=androidx.compose.ui.Alignment.CenterVertically) {
                RadioButton(selected=!publicContribution,onClick={publicContribution=false})
                Text("Privada temporal",modifier=Modifier.weight(1f))
            }
            Row(verticalAlignment=androidx.compose.ui.Alignment.CenterVertically) {
                RadioButton(selected=publicContribution,onClick={publicContribution=true})
                Text("Permitir publicar una copia de materiales al cerrar",modifier=Modifier.weight(1f))
            }
            if(publicContribution) Text("Solo material que tú autorices; no incluye mensajes ni trabajos de participantes.",style=MaterialTheme.typography.labelSmall)
        }
        ChemButton("Crear aula DEMO",{if(title.isNotBlank()&&topic.isNotBlank()&&validDate)created=true},Modifier.fillMaxWidth())
        if(!validDate && expires.isNotEmpty()) Text("Introduce una fecha futura con el formato AAAA-MM-DD.",color=MaterialTheme.colorScheme.error)
        if(created) GroupPanel("Aula preparada · Ejemplo",title) {
            SimpleInfo("Tema",topic)
            SimpleInfo("Expira",expires)
            SimpleInfo("Modalidad",if(publicContribution) "Copia autorizable" else "Solo privada")
            DemoBadge("No guardada en ChemXR API")
        }
    }
}
