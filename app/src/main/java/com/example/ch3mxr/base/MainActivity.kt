package com.example.ch3mxr.base

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.*
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ch3mxr.R
import com.example.ch3mxr.core.model.*
import com.example.ch3mxr.core.ui.theme.*
import com.example.ch3mxr.feature.ads.ProWelcomeScreen
import com.example.ch3mxr.feature.auth.login.LoginScreen
import com.example.ch3mxr.feature.learning.*
import com.example.ch3mxr.feature.main.shell.MainShellScreen
import com.example.ch3mxr.feature.profile.model.DemoStudySpaces
import com.example.ch3mxr.feature.settings.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  private val preferences: PreferencesViewModel by viewModels()
  private val learning: LearningViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.Theme_Ch3mxr_Main)
    super.onCreate(savedInstanceState)
    WindowCompat.setDecorFitsSystemWindows(window, false)
    setContent {
      val context = LocalContext.current
      val preferenceState by preferences.state.collectAsStateWithLifecycle()
      val learningState by learning.state.collectAsStateWithLifecycle()
      LaunchedEffect(Unit) {
        preferences.errors.collect { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
      }
      LaunchedEffect(Unit) {
        learning.errors.collect { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
      }
      val p = (preferenceState as? LoadState.Ready)?.value
      val l = (learningState as? LoadState.Ready)?.value
      if (p == null || l == null) {
        ChemXRTheme {
          Box(Modifier.fillMaxSize().systemBarsPadding(), contentAlignment = Alignment.Center) {
            val error =
              (preferenceState as? LoadState.Failed)?.message
                ?: (learningState as? LoadState.Failed)?.message
            if (error == null) CircularProgressIndicator()
            else
              Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(error)
                Button(
                  onClick = {
                    preferences.retry()
                    learning.retry()
                  }
                ) {
                  Text("Reintentar")
                }
              }
          }
        }
      } else {
        val prefs = PreferencesUi(p, preferences)
        val store = LearningUiController(l, learning)
        val spaceId = prefs.getString("selected_space_id", "personal")
        val space =
          DemoStudySpaces.available.firstOrNull { it.id == spaceId }
            ?: DemoStudySpaces.available.first()
        val themeMode =
          runCatching { ThemeMode.valueOf(prefs.getString("theme_mode", "DARK")) }
            .getOrDefault(ThemeMode.DARK)
        val accent =
          runCatching { ChemAccent.valueOf(prefs.getString("personal_accent", "LIME")) }
            .getOrDefault(ChemAccent.LIME)
        var showMain by rememberSaveable { mutableStateOf(false) }
        var showProWelcome by rememberSaveable { mutableStateOf(false) }
        val isLight =
          when (themeMode) {
            ThemeMode.LIGHT -> true
            ThemeMode.DARK -> false
            ThemeMode.SYSTEM -> !androidx.compose.foundation.isSystemInDarkTheme()
          }
        SideEffect {
          WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = isLight
            isAppearanceLightNavigationBars = isLight
          }
        }
        ChemXRTheme(
          mode = themeMode,
          accentColor = if (space.academic) space.primary else accent.color,
        ) {
          CompositionLocalProvider(LocalLearning provides store, LocalPreferences provides prefs) {
            BackHandler(enabled = showMain) { showMain = false }
            if (showMain && showProWelcome && space.id == "personal" && !store.isPro) {
              ProWelcomeScreen(
                onPro = {
                  store.setPlan(StudentPlan.PRO)
                  showProWelcome = false
                },
                onSkip = { showProWelcome = false },
              )
            } else if (showMain) {
              MainShellScreen(
                themeMode = themeMode,
                accent = accent,
                selectedSpaceId = space.id,
                onSpaceChange = { id ->
                  if (DemoStudySpaces.available.any { it.id == id })
                    prefs.setString("selected_space_id", id)
                },
                onThemeChange = { prefs.setString("theme_mode", it.name) },
                onAccentChange = {
                  if (!space.academic) prefs.setString("personal_accent", it.name)
                },
                onLogout = {
                  showMain = false
                  showProWelcome = false
                },
              )
            } else {
              val comingSoon: () -> Unit = {
                Toast.makeText(context, "Disponible próximamente", Toast.LENGTH_SHORT).show()
              }
              LoginScreen(
                onLoginClick = { _, _, _ ->
                  showProWelcome = space.id == "personal" && !store.isPro
                  showMain = true
                },
                onForgotPasswordClick = comingSoon,
                onRegisterClick = comingSoon,
                onGoogleClick = comingSoon,
                onFacebookClick = comingSoon,
              )
            }
          }
        }
      }
    }
  }
}
