package com.example.ch3mxr.feature.main.shell

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.ch3mxr.core.model.*
import com.example.ch3mxr.core.ui.components.*
import com.example.ch3mxr.core.ui.theme.*
import com.example.ch3mxr.feature.ads.BottomAdBanner
import com.example.ch3mxr.feature.community.CourseForumScreen
import com.example.ch3mxr.feature.community.DiscussionScreen
import com.example.ch3mxr.feature.experience.*
import com.example.ch3mxr.feature.explore.ExploreCoursesScreen
import com.example.ch3mxr.feature.institution.ClassroomPage
import com.example.ch3mxr.feature.institution.InstitutionalCoursePage
import com.example.ch3mxr.feature.learning.*
import com.example.ch3mxr.feature.library.catalog.ResourceDetailPage
import com.example.ch3mxr.feature.main.agenda.AgendaScreen
import com.example.ch3mxr.feature.main.community.CommunityScreen
import com.example.ch3mxr.feature.main.components.ChemBottomBar
import com.example.ch3mxr.feature.main.courses.CoursesScreen
import com.example.ch3mxr.feature.main.home.HomeScreen
import com.example.ch3mxr.feature.notifications.*
import com.example.ch3mxr.feature.profile.*
import com.example.ch3mxr.feature.profile.model.*
import com.example.ch3mxr.feature.search.SearchScreen
import com.example.ch3mxr.feature.selection.CourseSelectionCatalog
import com.example.ch3mxr.feature.selection.CourseSelectionScreen
import com.example.ch3mxr.feature.settings.SettingsScreen
import com.example.ch3mxr.feature.tutor.TutorAiSheet

private enum class MainDestination {
  TABS,
  PROFILE,
  SETTINGS,
  SEARCH,
  NOTIFICATIONS,
  SELECTION,
  DETAIL,
}

private val CourseIdsSaver = listSaver<List<String>, String>(save = { it }, restore = { it })

/** Shell del prototipo: las features son mockups, NO administran matrículas/pagos reales. */
@Composable
fun MainShellScreen(
  themeMode: ThemeMode = ThemeMode.DARK,
  accent: ChemAccent = ChemAccent.LIME,
  selectedSpaceId: String = "personal",
  onSpaceChange: (String) -> Unit = {},
  onThemeChange: (ThemeMode) -> Unit = {},
  onAccentChange: (ChemAccent) -> Unit = {},
  onLogout: () -> Unit = {},
  startWithMembership: Boolean = false,
) {
  val learning = LocalLearning.current
  val context = LocalContext.current
  val prefs = com.example.ch3mxr.feature.settings.LocalPreferences.current
  val studySpaces = DemoStudySpaces.available
  val space = studySpaces.firstOrNull { it.id == selectedSpaceId } ?: studySpaces.first()
  var selectedTab by rememberSaveable { mutableStateOf(MainTab.HOME) }
  val startInMembership = startWithMembership && space.id == "personal"
  var destination by rememberSaveable {
    mutableStateOf(if (startInMembership) MainDestination.DETAIL else MainDestination.TABS)
  }
  var settingsFrom by rememberSaveable { mutableStateOf(MainDestination.PROFILE) }
  var detailFrom by rememberSaveable { mutableStateOf(MainDestination.TABS) }
  var selectionFrom by rememberSaveable { mutableStateOf(MainDestination.TABS) }
  // Límite conservador del prototipo: un intersticial de Agenda por sesión.
  var agendaInterstitialUsed by rememberSaveable { mutableStateOf(false) }
  var tutorVisible by rememberSaveable { mutableStateOf(false) }
  var spacePickerVisible by rememberSaveable { mutableStateOf(false) }
  val selectedCourseIds = learning.cartIds.toList()
  val purchasedCourseIds = learning.purchasedIds.toList()
  var activeClassId by rememberSaveable { mutableStateOf("") }
  val activeClass =
    ClassroomCatalog.find(activeClassId)?.takeIf { it.spaceId == space.id }
      ?: ClassroomCatalog.forSpace(space.id).firstOrNull()
  val selectedCourses = selectedCourseIds.mapNotNull { CourseSelectionCatalog.find(it) }
  val purchasedCourses = purchasedCourseIds.mapNotNull { CourseSelectionCatalog.find(it) }
  var routeStack by
    rememberSaveable(stateSaver = CourseIdsSaver) {
      mutableStateOf(if (startInMembership) listOf("membership") else emptyList())
    }
  val tabStates = rememberSaveableStateHolder()
  val openRoute: (String) -> Unit = { requested ->
    var route = requested
    var targetSpace = space.id
    if (route.startsWith("switchSpace/")) {
      val parts = route.split('/', limit = 3)
      if (parts.size == 3 && studySpaces.any { it.id == parts[1] }) {
        targetSpace = parts[1]
        onSpaceChange(targetSpace)
        route = parts[2]
        routeStack = emptyList()
        destination = MainDestination.TABS
        selectedTab = MainTab.HOME
      }
    }
    if (route == "selection" && targetSpace == "personal") {
      selectionFrom = destination
      destination = MainDestination.SELECTION
    } else {
      if (route == "course")
        route =
          if (targetSpace == "personal") "learnCourse/adrom_chemistry"
          else
            "institutionCourse/${activeClass?.id ?: ClassroomCatalog.forSpace(targetSpace).firstOrNull()?.id.orEmpty()}"
      if (route.startsWith("institutionCourse/"))
        activeClassId = route.substringAfter("institutionCourse/")
      if (destination != MainDestination.DETAIL) {
        routeStack = emptyList()
        detailFrom = destination
      }
      routeStack = routeStack + route
      destination = MainDestination.DETAIL
    }
  }
  val addSelectedCourse: (String) -> Unit = { id ->
    if (space.id == "personal") learning.addToCart(id)
  }
  val openSelection: () -> Unit = {
    if (space.id == "personal") {
      selectionFrom = destination
      destination = MainDestination.SELECTION
    }
  }
  val periodKey = "period_${space.id}"
  val period = prefs.getString(periodKey, space.periods.firstOrNull().orEmpty())
  val calendarSources =
    setOf("personal") + listOf("utp", "senati").filter { prefs.getBoolean("calendar_$it", false) }
  val notificationSources =
    setOf("personal") +
      listOf("utp", "senati").filter { prefs.getBoolean("notifications_$it", false) }
  val updateCalendar: (String, Boolean) -> Unit = { id, enabled ->
    prefs.setBoolean("calendar_$id", enabled)
  }
  val updateNotices: (String, Boolean) -> Unit = { id, enabled ->
    prefs.setBoolean("notifications_$id", enabled)
  }
  val goBack: () -> Unit = {
    when (destination) {
      MainDestination.DETAIL -> {
        if (routeStack.isNotEmpty()) routeStack = routeStack.dropLast(1)
        if (routeStack.isEmpty()) destination = detailFrom
      }
      MainDestination.SELECTION -> destination = selectionFrom
      MainDestination.SETTINGS -> destination = settingsFrom
      MainDestination.TABS -> Unit
      else -> destination = MainDestination.TABS
    }
  }
  BackHandler(enabled = destination != MainDestination.TABS && !tutorVisible) { goBack() }
  val currentRoute = routeStack.lastOrNull().orEmpty()
  val settingsAction: () -> Unit = {
    settingsFrom = destination
    destination = MainDestination.SETTINGS
  }
  Scaffold(
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    containerColor = MaterialTheme.colorScheme.background,
    topBar = {
      when (destination) {
        MainDestination.TABS ->
          ChemHomeTopBar(
            searchPlaceholder = selectedTab.searchHint,
            activeSpace = space,
            onSpaceClick = { spacePickerVisible = true },
            onProfileClick = { destination = MainDestination.PROFILE },
            onSearchClick = { destination = MainDestination.SEARCH },
            onAiClick = { if (learning.tutorAvailable) tutorVisible = true },
            showTutor = learning.tutorAvailable,
            onSelectionClick = openSelection,
            selectionCount = selectedCourses.size,
            onNotificationsClick = { destination = MainDestination.NOTIFICATIONS },
          )
        MainDestination.PROFILE ->
          ChemDetailTopBar(
            space.name,
            goBack,
            actionIcon = Icons.Outlined.Settings,
            actionDescription = "Configuración",
            onAction = settingsAction,
            onTitleClick = { spacePickerVisible = true },
          )
        MainDestination.SETTINGS -> ChemDetailTopBar("Configuración", goBack)
        MainDestination.NOTIFICATIONS -> NotificationsTopBar(goBack)
        MainDestination.SELECTION -> ChemDetailTopBar("Mi selección", goBack)
        MainDestination.DETAIL ->
          ChemDetailTopBar(
            title =
              when {
                currentRoute == "explore" -> "Explorar cursos"
                currentRoute.startsWith("learnCourse/") -> "Curso"
                currentRoute.startsWith("learningContent/") -> "Tu aprendizaje"
                currentRoute.startsWith("institutionCourse/") -> "Asignatura"
                currentRoute.startsWith("classroom/") -> "Salón y grabación"
                currentRoute.startsWith("resource/") -> "Recurso de biblioteca"
                currentRoute.startsWith("discussion/") -> "Publicación y comentarios"
                currentRoute.startsWith("personalComments/") -> "Comentarios"
                else -> experienceTitle(currentRoute)
              },
            onBack = goBack,
            actionIcon =
              if (currentRoute == "explore" && space.id == "personal") Icons.Outlined.ShoppingCart
              else null,
            actionDescription = "Mi selección, ${selectedCourses.size} cursos",
            onAction = openSelection,
            onAiClick = if (learning.tutorAvailable) ({ tutorVisible = true }) else null,
          )
        MainDestination.SEARCH -> Unit
      }
    },
    bottomBar = {
      if (destination == MainDestination.TABS) {
        Column {
          if (learning.adsAllowed(space.id)) BottomAdBanner()
          ChemBottomBar(selectedTab, space) { selectedTab = it }
        }
      }
    },
  ) { insets ->
    Box(Modifier.fillMaxSize().padding(insets)) {
      val modifier = Modifier.fillMaxSize()
      when (destination) {
        MainDestination.TABS -> {
          // Evita volver a cargar estados de otro espacio.
          tabStates.SaveableStateProvider("${space.id}_${selectedTab.name}") {
            when (selectedTab) {
              MainTab.HOME ->
                if (space.academic) InstitutionHomeScreen(space, period, modifier, openRoute)
                else HomeScreen(modifier, onOpen = openRoute)
              MainTab.COURSES ->
                CoursesScreen(
                  modifier,
                  space,
                  onOpen = openRoute,
                  purchasedCourses =
                    if (space.id == "personal") purchasedCourses.map { it.course } else emptyList(),
                )
              MainTab.CAMPUS ->
                if (space.academic) CampusHubScreen(space, modifier, openRoute)
                else HomeScreen(modifier, onOpen = openRoute)
              MainTab.COMMUNITY -> CommunityScreen(modifier, space, onOpen = openRoute)
              MainTab.AGENDA ->
                AgendaScreen(
                  modifier,
                  space,
                  calendarSources,
                  updateCalendar,
                  allowCompletionAd = learning.adsAllowed(space.id) && !agendaInterstitialUsed,
                  onCompletionAdShown = { agendaInterstitialUsed = true },
                  onOpen = openRoute,
                )
            }
          }
        }
        MainDestination.PROFILE ->
          ProfileScreen(
            modifier = modifier,
            activeSpace = space,
            activePeriod = period,
            onPeriodChange = { newPeriod -> prefs.setString(periodKey, newPeriod) },
            onSettings = settingsAction,
            onLogout = onLogout,
            onOpen = openRoute,
            onMockAction = { title ->
              Toast.makeText(context, "$title: demostración", Toast.LENGTH_SHORT).show()
            },
          )
        MainDestination.SETTINGS ->
          SettingsScreen(
            modifier = modifier,
            themeMode = themeMode,
            accent = accent,
            academicSpace = space.academic,
            onThemeChange = onThemeChange,
            onAccentChange = onAccentChange,
            onOpen = openRoute,
            onMockAction = { title ->
              Toast.makeText(context, "$title: maqueta", Toast.LENGTH_SHORT).show()
            },
          )
        MainDestination.NOTIFICATIONS ->
          NotificationsScreen(modifier, space, notificationSources, updateNotices, settingsAction)
        MainDestination.SEARCH ->
          SearchScreen(modifier, selectedTab.title, goBack, onResultClick = { openRoute("course") })
        MainDestination.SELECTION ->
          if (space.id == "personal")
            CourseSelectionScreen(
              modifier = modifier,
              selectedCourses = selectedCourses,
              availableCourses =
                CourseSelectionCatalog.courses.filter {
                  it.id !in selectedCourseIds && it.id !in purchasedCourseIds
                },
              onAdd = addSelectedCourse,
              onRemove = { id -> learning.removeFromCart(id) },
              onExplore = {
                if (selectionFrom == MainDestination.DETAIL && currentRoute == "explore")
                  destination = MainDestination.DETAIL
                else {
                  destination = MainDestination.TABS
                  openRoute("explore")
                }
              },
              onCheckout = {
                learning.checkout { saved ->
                  if (saved) {
                    selectedTab = MainTab.COURSES
                    // Mostrar todos los cursos tras la compra, incluso si había filtros activos.
                    tabStates.removeState("${space.id}_${MainTab.COURSES.name}")
                    destination = MainDestination.TABS
                    Toast.makeText(
                        context,
                        "Compra simulada: cursos añadidos a Mis cursos.",
                        Toast.LENGTH_SHORT,
                      )
                      .show()
                  }
                }
              },
            )
        MainDestination.DETAIL ->
          when {
            space.id == "personal" && currentRoute == "explore" ->
              ExploreCoursesScreen(
                modifier = modifier,
                onSelection = openSelection,
                onOpenCourse = { openRoute("learnCourse/$it") },
              )
            space.id == "personal" && currentRoute.startsWith("learnCourse/") ->
              LearningCourseDetail(currentRoute.substringAfter("learnCourse/"), modifier, openRoute)
            space.id == "personal" && currentRoute.startsWith("learningContent/") ->
              LearningContentPage(
                currentRoute.substringAfter("learningContent/"),
                modifier,
                openRoute,
              )
            currentRoute.startsWith("media/") -> {
              val parts = currentRoute.split('/', limit = 3)
              if (parts.size == 3)
                com.example.ch3mxr.feature.media.MediaResourcePage(
                  parts[2],
                  space.id,
                  parts[1],
                  openRoute,
                  modifier,
                )
            }
            currentRoute.startsWith("resource/") ->
              ResourceDetailPage(
                currentRoute.substringAfter("resource/"),
                space,
                modifier,
                openRoute,
              )
            currentRoute.startsWith("discussion/") ->
              DiscussionScreen(
                currentRoute.substringAfter("discussion/"),
                space,
                modifier,
                openRoute,
              )
            space.id == "personal" && currentRoute.startsWith("personalComments/") ->
              DiscussionScreen(
                currentRoute.substringAfter("personalComments/"),
                space,
                modifier,
                openRoute,
              )
            space.academic && currentRoute.startsWith("institutionCourse/") ->
              InstitutionalCoursePage(
                currentRoute.substringAfter("institutionCourse/"),
                space,
                modifier,
                openRoute,
              )
            space.academic && currentRoute.startsWith("classroom/") ->
              ClassroomPage(currentRoute.substringAfter("classroom/"), space, modifier, openRoute)
            space.academic && currentRoute == "forum" ->
              CourseForumScreen(activeClass?.id.orEmpty(), space, modifier, openRoute)
            space.academic && currentRoute == "sessions" ->
              ClassroomPage(activeClass?.id.orEmpty(), space, modifier, openRoute)
            else -> ExperiencePageScreen(currentRoute, space, period, modifier, openRoute)
          }
      }
    }
  }
  LaunchedEffect(learning.isPro) { if (!learning.isPro) tutorVisible = false }
  if (spacePickerVisible) {
    StudySpacePickerDialog(
      spaces = studySpaces,
      selectedId = space.id,
      onSelect = { id ->
        onSpaceChange(id)
        selectedTab = MainTab.HOME
        routeStack = emptyList()
        destination = MainDestination.TABS
      },
      onDismiss = { spacePickerVisible = false },
    )
  }
  if (tutorVisible && learning.tutorAvailable) {
    val label =
      when {
        currentRoute.startsWith("learnCourse/") || currentRoute.startsWith("learningContent/") ->
          LearningCatalog.find(currentRoute.substringAfter('/'))?.title ?: "Curso"
        currentRoute.startsWith("institutionCourse/") || currentRoute.startsWith("classroom/") ->
          ClassroomCatalog.find(currentRoute.substringAfter('/'))?.title ?: "Curso institucional"
        destination == MainDestination.DETAIL -> experienceTitle(currentRoute)
        else -> selectedTab.title
      }
    TutorAiSheet(contextLabel = "${space.name} · $label", onDismiss = { tutorVisible = false })
  }
}
