package com.example.ch3mxr.feature.main.shell

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.example.ch3mxr.feature.main.components.ChemBottomBar
import com.example.ch3mxr.core.ui.components.ChemHomeTopBar
import com.example.ch3mxr.feature.main.home.HomeScreen
import com.example.ch3mxr.feature.main.courses.CoursesScreen
import com.example.ch3mxr.feature.main.community.CommunityScreen
import com.example.ch3mxr.feature.main.agenda.AgendaScreen
import com.example.ch3mxr.feature.notifications.NotificationsScreen
import com.example.ch3mxr.feature.notifications.NotificationsTopBar

@Composable
fun MainShellScreen() {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.HOME) }
    var notificationsVisible by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = notificationsVisible) { notificationsVisible = false }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (notificationsVisible) {
                NotificationsTopBar(onBackClick = { notificationsVisible = false })
            } else {
                ChemHomeTopBar(
                    searchPlaceholder = selectedTab.searchHint,
                    onNotificationsClick = { notificationsVisible = true }
                )
            }
        },
        bottomBar = {
            if (!notificationsVisible) {
                ChemBottomBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
            }
        }
    ) { insets ->
        val modifier = Modifier.fillMaxSize().padding(insets)
        if (notificationsVisible) {
            NotificationsScreen(modifier)
        } else {
            when (selectedTab) {
                MainTab.HOME -> HomeScreen(modifier)
                MainTab.COURSES -> CoursesScreen(modifier)
                MainTab.COMMUNITY -> CommunityScreen(modifier)
                MainTab.AGENDA -> AgendaScreen(modifier)
            }
        }
    }
}
