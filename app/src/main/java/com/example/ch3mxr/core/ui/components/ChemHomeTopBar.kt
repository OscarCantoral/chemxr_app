package com.example.ch3mxr.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.theme.ChemDimens
import com.example.ch3mxr.core.ui.theme.ChemShapes
import com.example.ch3mxr.feature.experience.SpaceMark
import com.example.ch3mxr.feature.profile.model.StudySpace

@Composable
fun ChemHomeTopBar(
    searchPlaceholder:String,
    onProfileClick:()->Unit={}, onSearchClick:()->Unit={}, onAiClick:()->Unit={},
    onNotificationsClick:()->Unit,
    activeSpace: StudySpace? = null,
    onSpaceClick:()->Unit={}
) {
    val c=MaterialTheme.colorScheme
    Column(Modifier.fillMaxWidth().background(c.background).statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().height(ChemDimens.TopBarHeight).padding(horizontal=ChemDimens.TopBarHorizontalPadding),
            verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(7.dp)) {
            ChemProfileAvatar(onClick=onProfileClick)
            Surface(
                modifier=Modifier.weight(1f).height(ChemDimens.TopBarSearchHeight).clickable(onClick=onSearchClick),
                shape=ChemShapes.Component,color=c.surface,border=BorderStroke(1.dp,c.outline.copy(alpha=.32f))
            ) {
                Row(Modifier.padding(horizontal=7.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Outlined.Search,null,Modifier.size(17.dp),tint=c.onSurfaceVariant)
                    Text(searchPlaceholder,style=MaterialTheme.typography.labelSmall,color=c.onSurfaceVariant,maxLines=1,overflow=TextOverflow.Ellipsis)
                }
            }
            ChemAiButton(onClick=onAiClick)
            Box(Modifier.size(ChemDimens.TopBarActionSize).clickable(onClick=onNotificationsClick),contentAlignment=Alignment.Center) {
                Icon(Icons.Outlined.Notifications,"Notificaciones",Modifier.size(23.dp),tint=c.onBackground)
                Box(Modifier.align(Alignment.TopEnd).size(5.dp).background(c.primary,ChemShapes.Component))
            }
        }
        if(activeSpace!=null) {
            Row(
                Modifier.fillMaxWidth().clickable(onClick=onSpaceClick).padding(horizontal=15.dp,vertical=6.dp),
                verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)
            ) {
                SpaceMark(activeSpace,22.dp)
                Text(activeSpace.name,style=MaterialTheme.typography.labelMedium,color=c.onBackground)
                Icon(Icons.Outlined.KeyboardArrowDown,"Cambiar espacio",Modifier.size(17.dp),tint=c.primary)
                if(activeSpace.demo) Text("DEMO",style=MaterialTheme.typography.labelSmall,color=c.onSurfaceVariant)
            }
        }
        HorizontalDivider(color=c.outline.copy(alpha=.30f),thickness=1.dp)
    }
}
