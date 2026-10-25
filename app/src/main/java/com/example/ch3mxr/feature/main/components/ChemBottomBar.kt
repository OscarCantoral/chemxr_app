package com.example.ch3mxr.feature.main.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.theme.ChemDimens
import com.example.ch3mxr.feature.experience.SpaceMark
import com.example.ch3mxr.feature.profile.model.StudySpace
import com.example.ch3mxr.feature.main.shell.MainTab

@Composable
fun ChemBottomBar(selectedTab: MainTab, activeSpace: StudySpace, onTabSelected: (MainTab)->Unit) {
    val colors=MaterialTheme.colorScheme
    val tabs=if(activeSpace.academic) MainTab.entries.toList() else MainTab.entries.filter { it!=MainTab.CAMPUS }
    Column(Modifier.fillMaxWidth().background(colors.background).navigationBarsPadding()) {
        HorizontalDivider(color=colors.outline.copy(alpha=.30f))
        Row(Modifier.fillMaxWidth().height(ChemDimens.BottomBarHeight),verticalAlignment=Alignment.CenterVertically) {
            tabs.forEach { tab ->
                val selected=tab==selectedTab
                val tint=if(selected) colors.primary else colors.onSurfaceVariant
                Column(Modifier.weight(if(tab==MainTab.CAMPUS) 1.18f else 1f).fillMaxHeight().clickable {onTabSelected(tab)},
                    horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center) {
                    if(tab==MainTab.CAMPUS) {
                        Surface(color=if(selected) colors.primary.copy(alpha=.13f) else colors.surface,
                            shape=androidx.compose.ui.graphics.RectangleShape,
                            border=BorderStroke(if(selected) 2.dp else 1.dp,if(selected) colors.primary else colors.outline.copy(alpha=.3f))) {
                            Box(Modifier.size(48.dp),contentAlignment=Alignment.Center) {SpaceMark(activeSpace,39.dp)}
                        }
                    } else {
                        Icon(tab.icon,tab.title,Modifier.size(22.dp),tint=tint)
                        Spacer(Modifier.height(3.dp))
                        Text(tab.title,color=tint,maxLines=1,style=MaterialTheme.typography.labelSmall,
                            fontWeight=if(selected) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        }
    }
}
