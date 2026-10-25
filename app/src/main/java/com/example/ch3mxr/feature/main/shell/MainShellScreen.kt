package com.example.ch3mxr.feature.main.shell

import android.content.Context
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
import com.example.ch3mxr.core.ui.components.*
import com.example.ch3mxr.core.ui.theme.*
import com.example.ch3mxr.feature.experience.*
import com.example.ch3mxr.feature.main.agenda.AgendaScreen
import com.example.ch3mxr.feature.main.community.CommunityScreen
import com.example.ch3mxr.feature.main.components.ChemBottomBar
import com.example.ch3mxr.feature.main.courses.CoursesScreen
import com.example.ch3mxr.feature.main.home.HomeScreen
import com.example.ch3mxr.feature.notifications.*
import com.example.ch3mxr.feature.profile.*
import com.example.ch3mxr.feature.profile.model.*
import com.example.ch3mxr.feature.search.SearchScreen
import com.example.ch3mxr.feature.settings.SettingsScreen
import com.example.ch3mxr.feature.tutor.TutorAiSheet

private enum class MainDestination { TABS, PROFILE, SETTINGS, SEARCH, NOTIFICATIONS, DETAIL }

/** Shell del prototipo: las features son mockups, NO administran matrículas/pagos reales. */
@Composable
fun MainShellScreen(
    themeMode:ThemeMode=ThemeMode.DARK, accent:ChemAccent=ChemAccent.LIME,
    selectedSpaceId:String="personal", onSpaceChange:(String)->Unit={},
    onThemeChange:(ThemeMode)->Unit={}, onAccentChange:(ChemAccent)->Unit={}, onLogout:()->Unit={}
) {
    val context=LocalContext.current
    val prefs=remember {context.getSharedPreferences("chemxr_demo_preferences",Context.MODE_PRIVATE)}
    val studySpaces=DemoStudySpaces.available
    val space=studySpaces.firstOrNull {it.id==selectedSpaceId} ?: studySpaces.first()
    var selectedTab by rememberSaveable {mutableStateOf(MainTab.HOME)}
    var destination by rememberSaveable {mutableStateOf(MainDestination.TABS)}
    var settingsFrom by remember {mutableStateOf(MainDestination.PROFILE)}
    var detailFrom by remember {mutableStateOf(MainDestination.TABS)}
    var tutorVisible by rememberSaveable {mutableStateOf(false)}
    var spacePickerVisible by rememberSaveable {mutableStateOf(false)}
    val routeStack=remember {mutableStateListOf<String>()}
    val tabStates=rememberSaveableStateHolder()
    val openRoute:(String)->Unit={route ->
        if(destination!=MainDestination.DETAIL) {
            routeStack.clear()
            detailFrom=destination
        }
        routeStack.add(route)
        destination=MainDestination.DETAIL
    }
    val periodKey="period_${space.id}"
    var period by remember(space.id) {mutableStateOf(prefs.getString(periodKey,space.periods.firstOrNull().orEmpty()) ?: "")}
    var calendarSources by remember {mutableStateOf(setOf("personal") + listOf("utp","senati").filter {prefs.getBoolean("calendar_$it",false)})}
    var notificationSources by remember {mutableStateOf(setOf("personal") + listOf("utp","senati").filter {prefs.getBoolean("notifications_$it",false)})}
    val updateCalendar:(String,Boolean)->Unit={id,enabled ->
        calendarSources = if(enabled)calendarSources+id else calendarSources-id
        prefs.edit().putBoolean("calendar_$id",enabled).apply()
    }
    val updateNotices:(String,Boolean)->Unit={id,enabled ->
        notificationSources=if(enabled)notificationSources+id else notificationSources-id
        prefs.edit().putBoolean("notifications_$id",enabled).apply()
    }
    val goBack:()->Unit={
        when(destination) {
            MainDestination.DETAIL -> {
                if(routeStack.isNotEmpty()) routeStack.removeAt(routeStack.lastIndex)
                if(routeStack.isEmpty()) destination=detailFrom
            }
            MainDestination.SETTINGS -> destination=settingsFrom
            MainDestination.TABS -> Unit
            else -> destination=MainDestination.TABS
        }
    }
    BackHandler(enabled=destination!=MainDestination.TABS && !tutorVisible) {goBack()}
    val currentRoute=routeStack.lastOrNull().orEmpty()
    val settingsAction:()->Unit={settingsFrom=destination;destination=MainDestination.SETTINGS}
    Scaffold(
        contentWindowInsets=WindowInsets(0,0,0,0),
        containerColor=MaterialTheme.colorScheme.background,
        topBar={
            when(destination) {
                MainDestination.TABS -> ChemHomeTopBar(
                    searchPlaceholder=selectedTab.searchHint,
                    activeSpace=space,onSpaceClick={spacePickerVisible=true},
                    onProfileClick={destination=MainDestination.PROFILE},
                    onSearchClick={destination=MainDestination.SEARCH},
                    onAiClick={tutorVisible=true},
                    onNotificationsClick={destination=MainDestination.NOTIFICATIONS}
                )
                MainDestination.PROFILE -> ChemDetailTopBar(space.name,goBack,
                    actionIcon=Icons.Outlined.Settings,actionDescription="Configuración",onAction=settingsAction,
                    onTitleClick={spacePickerVisible=true})
                MainDestination.SETTINGS -> ChemDetailTopBar("Configuración",goBack)
                MainDestination.NOTIFICATIONS -> NotificationsTopBar(goBack)
                MainDestination.DETAIL -> ChemDetailTopBar(experienceTitle(currentRoute),goBack)
                MainDestination.SEARCH -> Unit
            }
        },
        bottomBar={ if(destination==MainDestination.TABS) ChemBottomBar(selectedTab,space) {selectedTab=it} }
    ) {insets ->
        Box(Modifier.fillMaxSize().padding(insets)) {
            val modifier=Modifier.fillMaxSize()
            when(destination) {
                MainDestination.TABS -> {
                    // Evita volver a cargar estados de otro espacio.
                    tabStates.SaveableStateProvider("${space.id}_${selectedTab.name}") {
                        when(selectedTab) {
                            MainTab.HOME -> if(space.academic) InstitutionHomeScreen(space,period,modifier,openRoute)
                            else HomeScreen(modifier,onOpen=openRoute)
                            MainTab.COURSES -> CoursesScreen(modifier,space,onOpen=openRoute)
                            MainTab.CAMPUS -> if(space.academic) CampusHubScreen(space,modifier,openRoute)
                                else HomeScreen(modifier,onOpen=openRoute)
                            MainTab.COMMUNITY -> CommunityScreen(modifier,space,onOpen=openRoute)
                            MainTab.AGENDA -> AgendaScreen(modifier,space,calendarSources,updateCalendar)
                        }
                    }
                }
                MainDestination.PROFILE -> ProfileScreen(
                    modifier=modifier,activeSpace=space,activePeriod=period,
                    onPeriodChange={newPeriod ->period=newPeriod;prefs.edit().putString(periodKey,newPeriod).apply()},
                    onSettings=settingsAction,onLogout=onLogout,onOpen=openRoute,
                    onMockAction={ title ->Toast.makeText(context,"$title: demostración",Toast.LENGTH_SHORT).show()}
                )
                MainDestination.SETTINGS -> SettingsScreen(
                    modifier=modifier,themeMode=themeMode,accent=accent,academicSpace=space.academic,
                    onThemeChange=onThemeChange,onAccentChange=onAccentChange,
                    onOpen=openRoute,onMockAction={title->Toast.makeText(context,"$title: maqueta",Toast.LENGTH_SHORT).show()}
                )
                MainDestination.NOTIFICATIONS -> NotificationsScreen(
                    modifier,space,notificationSources,updateNotices,settingsAction
                )
                MainDestination.SEARCH -> SearchScreen(modifier,selectedTab.title,goBack,
                    onResultClick={openRoute("course")})
                MainDestination.DETAIL -> ExperiencePageScreen(currentRoute,space,period,modifier,openRoute)
            }
        }
    }
    if(spacePickerVisible) {
        StudySpacePickerDialog(
            spaces=studySpaces,selectedId=space.id,
            onSelect={id ->
                onSpaceChange(id)
                selectedTab=MainTab.HOME
                routeStack.clear()
                destination=MainDestination.TABS
            },onDismiss={spacePickerVisible=false}
        )
    }
    if(tutorVisible && destination==MainDestination.TABS) {
        TutorAiSheet(contextLabel="${space.name} · ${selectedTab.title}",onDismiss={tutorVisible=false})
    }
}
