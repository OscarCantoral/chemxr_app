package com.example.ch3mxr.feature.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ch3mxr.core.ui.components.*
import com.example.ch3mxr.core.ui.theme.ChemDimens
import com.example.ch3mxr.feature.experience.*
import com.example.ch3mxr.feature.main.components.*
import com.example.ch3mxr.feature.profile.model.StudySpace

@Composable
fun ProfileScreen(
    modifier:Modifier=Modifier,activeSpace:StudySpace,onSettings:()->Unit,onLogout:()->Unit,
    onMockAction:(String)->Unit,
    activePeriod:String="",onPeriodChange:(String)->Unit={},onOpen:(String)->Unit={}
) {
    val c=MaterialTheme.colorScheme
    var periodMenu by remember {mutableStateOf(false)}
    MainPage(modifier) {
        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(15.dp)) {
            ChemProfileAvatar(onClick={onMockAction("Cambiar fotografía")},modifier=Modifier.size(ChemDimens.ProfileAvatarSize),avatarSize=ChemDimens.ProfileAvatarSize)
            Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(6.dp)) {
                Text("Oscar Cantoral",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.ExtraBold,color=c.onBackground)
                Text("@oscarcantoral",style=MaterialTheme.typography.bodySmall,color=c.onSurfaceVariant)
            }
        }
        Text("Ciencia, tecnología y aprendizaje",color=c.onSurfaceVariant)
        Row(horizontalArrangement=Arrangement.spacedBy(9.dp)) {
            ChemButton("Editar perfil",{onOpen("personalData")},Modifier.weight(1f))
            ChemButton("Perfil público",{onMockAction("Perfil público")},Modifier.weight(1f),style=ChemButtonStyle.OUTLINED)
        }
        SpaceContextHeader(activeSpace,"Espacio activo · Puedes cambiarlo desde el título")
        if(activeSpace.academic) {
            GroupPanel("Estado académico · DEMO", "Identidad académica del espacio seleccionado") {
                SimpleInfo("Código",activeSpace.studentCode)
                SimpleInfo("Carrera",activeSpace.program)
                SimpleInfo("Sede",activeSpace.campus)
                Box {
                    ChemButton("Periodo: $activePeriod  ▾",{periodMenu=true},Modifier.fillMaxWidth(),style=ChemButtonStyle.OUTLINED)
                    DropdownMenu(expanded=periodMenu,onDismissRequest={periodMenu=false}) {
                        activeSpace.periods.forEach { item -> DropdownMenuItem(text={Text(item)},onClick={onPeriodChange(item);periodMenu=false}) }
                    }
                }
            }
            ChemMenuGroup {
                ChemMenuRow(Icons.Outlined.Badge,"Carnet virtual","Código y datos institucionales · DEMO",{onOpen("studentCard")})
                ChemMenuRow(Icons.Outlined.AppRegistration,"Matrícula","Cursos, créditos y periodo",{onOpen("enrollment")})
                ChemMenuRow(Icons.Outlined.AccountBalance,"Cuenta corriente","Pagos y recibos",{onOpen("finance")})
                ChemMenuRow(Icons.Outlined.Description,"Documentos institucionales","Normativa oficial",{onOpen("documents")},showDivider=false)
            }
        }
        Eyebrow("Mi aprendizaje · ${activeSpace.name}")
        ChemMenuGroup {
            ChemMenuRow(Icons.Outlined.EmojiEvents,"Logros","Insignias y certificaciones",{onOpen("achievements")})
            ChemMenuRow(Icons.Outlined.Bookmarks,"Mi estante","Lecturas, favoritos y apuntes",{onOpen("shelf")})
            ChemMenuRow(Icons.Outlined.Forum,"Mis consultas","Mensajes académicos",{onOpen("messages")},showDivider=false)
        }
        GroupPanel("Mi membresía ChemXR", "Se gestiona independientemente del campus") {
            Text("Gratuito",fontSize=25.sp,fontWeight=FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            ChemButton("Ver membresías y beneficios",{onOpen("membership")},Modifier.fillMaxWidth(),style=ChemButtonStyle.OUTLINED)
        }
        if(!activeSpace.academic) DemoNavRow("Quiero enseñar","Crear un aula gratuita temporal",Icons.Outlined.School) {onOpen("privateClass")}
        DemoNavRow("Ayuda y soporte","Centro de atención y asistencia",Icons.Outlined.SupportAgent) {onOpen("support")}
        ChemButton("Configuración",onSettings,Modifier.fillMaxWidth(),style=ChemButtonStyle.OUTLINED,leadingIcon=Icons.Outlined.Settings)
        TextButton(onClick=onLogout) {
            Icon(Icons.Outlined.Logout,null,tint=c.error)
            Spacer(Modifier.width(10.dp))
            Text("Cerrar sesión",color=c.error)
        }
    }
}
