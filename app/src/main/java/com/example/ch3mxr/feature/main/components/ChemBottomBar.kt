package com.example.ch3mxr.feature.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.feature.main.shell.MainTab
import com.example.ch3mxr.core.ui.theme.ChemDimens

@Composable
fun ChemBottomBar(selectedTab: MainTab, onTabSelected: (MainTab) -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = Modifier.fillMaxWidth().background(colors.background).navigationBarsPadding()) {
        HorizontalDivider(color = colors.outline.copy(alpha = 0.30f))
        Row(modifier = Modifier.fillMaxWidth().height(ChemDimens.BottomBarHeight), verticalAlignment = Alignment.CenterVertically) {
            MainTab.entries.forEach { tab ->
                val selected = selectedTab == tab
                val tint = if (selected) colors.primary else colors.onSurfaceVariant
                val interaction = remember(tab) { MutableInteractionSource() }
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight().clickable(
                        interactionSource = interaction,
                        indication = null,
                        role = Role.Tab,
                        onClick = { if (!selected) onTabSelected(tab) }
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(tab.icon, tab.title, modifier = Modifier.size(22.dp), tint = tint)
                    Spacer(Modifier.height(3.dp))
                    Text(
                        tab.title, color = tint, maxLines = 1,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
