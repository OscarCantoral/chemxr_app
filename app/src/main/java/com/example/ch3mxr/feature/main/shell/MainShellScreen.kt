package com.example.ch3mxr.feature.main.shell

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.ch3mxr.core.ui.components.ChemDetailTopBar
import com.example.ch3mxr.core.ui.components.ChemHomeTopBar
import com.example.ch3mxr.core.ui.theme.ChemAccent
import com.example.ch3mxr.core.ui.theme.ThemeMode
import com.example.ch3mxr.feature.main.components.ChemBottomBar
import com.example.ch3mxr.feature.main.home.HomeScreen
import com.example.ch3mxr.feature.main.courses.CoursesScreen
import com.example.ch3mxr.feature.main.community.CommunityScreen
import com.example.ch3mxr.feature.main.agenda.AgendaScreen
import com.example.ch3mxr.feature.notifications.NotificationsScreen
import com.example.ch3mxr.feature.notifications.NotificationsTopBar
import com.example.ch3mxr.feature.profile.ProfileScreen
import com.example.ch3mxr.feature.settings.SettingsScreen
import com.example.ch3mxr.feature.search.SearchScreen
import com.example.ch3mxr.feature.tutor.TutorAiSheet

private enum class MainDestination { TABS, PROFILE, SETTINGS, SEARCH, NOTIFICATIONS }

@Composable
fun MainShellScreen(
    themeMode: ThemeMode = ThemeMode.DARK,
    accent: ChemAccent = ChemAccent.LIME,
    onThemeChange: (ThemeMode) -> Unit = {},
    onAccentChange: (ChemAccent) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.HOME) }
    var destination by rememberSaveable { mutableStateOf(MainDestination.TABS) }
    var tutorVisible by rememberSaveable { mutableStateOf(false) }
    val holder = rememberSaveableStateHolder()
    val context = LocalContext.current
    val demoAction: (String) -> Unit = { title ->
        Toast.makeText(context, "$title: disponible próximamente", Toast.LENGTH_SHORT).show()
    }
    val goBack = {
        destination = if (destination == MainDestination.SETTINGS) MainDestination.PROFILE else MainDestination.TABS
    }
    BackHandler(enabled = destination != MainDestination.TABS && !tutorVisible) { goBack() }

    Scaffold(
        contentWindowInsets = WindowInsets(0,0,0,0),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            when (destination) {
                MainDestination.TABS -> ChemHomeTopBar(
                    searchPlaceholder = selectedTab.searchHint,
                    onProfileClick = { destination = MainDestination.PROFILE },
                    onSearchClick = { destination = MainDestination.SEARCH },
                    onAiClick = { tutorVisible = true },
                    onNotificationsClick = { destination = MainDestination.NOTIFICATIONS }
                )
                MainDestination.PROFILE -> ChemDetailTopBar("Mi espacio", goBack,
                    actionIcon = Icons.Outlined.Settings, actionDescription = "Configuración",
                    onAction = { destination = MainDestination.SETTINGS })
                MainDestination.SETTINGS -> ChemDetailTopBar("Configuración", goBack)
                MainDestination.NOTIFICATIONS -> NotificationsTopBar(onBackClick = goBack)
                MainDestination.SEARCH -> Unit // SearchScreen trae su propia barra editable.
            }
        },
        bottomBar = {
            if (destination == MainDestination.TABS) {
                ChemBottomBar(selectedTab) { selectedTab = it }
            }
        }
    ) { insets ->
        val modifier = Modifier.fillMaxSize().padding(insets)
        when (destination) {
            MainDestination.TABS -> holder.SaveableStateProvider(selectedTab.name) {
                when (selectedTab) {
                    MainTab.HOME -> HomeScreen(modifier)
                    MainTab.COURSES -> CoursesScreen(modifier)
                    MainTab.COMMUNITY -> CommunityScreen(modifier)
                    MainTab.AGENDA -> AgendaScreen(modifier)
                }
            }
            MainDestination.PROFILE -> ProfileScreen(modifier,
                onSettings = { destination = MainDestination.SETTINGS },
                onLogout = onLogout, onMockAction = demoAction)
            MainDestination.SETTINGS -> SettingsScreen(modifier, themeMode, accent,
                onThemeChange, onAccentChange, demoAction)
            MainDestination.SEARCH -> SearchScreen(modifier,
                searchContext = selectedTab.title,
                onBack = goBack, onResultClick = demoAction)
            MainDestination.NOTIFICATIONS -> NotificationsScreen(modifier)
        }
    }
    if (tutorVisible && destination == MainDestination.TABS) {
        TutorAiSheet(contextLabel = "Mi espacio · ${selectedTab.title}",
            onDismiss = { tutorVisible = false })
    }
}
