package com.example.ch3mxr.base

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ch3mxr.R
import com.example.ch3mxr.core.ui.theme.ChemAccent
import com.example.ch3mxr.core.ui.theme.ChemXRTheme
import com.example.ch3mxr.core.ui.theme.ThemeMode
import com.example.ch3mxr.feature.auth.login.LoginScreen
import com.example.ch3mxr.feature.main.shell.MainShellScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Ch3mxr_Main)
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
        setContent {
            var showMain by rememberSaveable { mutableStateOf(false) }
            var themeMode by rememberSaveable { mutableStateOf(ThemeMode.DARK) }
            var accent by rememberSaveable { mutableStateOf(ChemAccent.LIME) }
            val context = LocalContext.current
            val comingSoon: () -> Unit = {
                Toast.makeText(context, "Disponible más adelante", Toast.LENGTH_SHORT).show()
            }
            val isLight = when(themeMode) {
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
            ChemXRTheme(mode = themeMode, accentColor = accent.color) {
                BackHandler(enabled = showMain) { showMain = false }
                if (showMain) {
                    MainShellScreen(
                        themeMode = themeMode, accent = accent,
                        onThemeChange = { themeMode = it },
                        onAccentChange = { accent = it },
                        onLogout = { showMain = false }
                    )
                } else {
                    LoginScreen(
                        onLoginClick = { _, _, _ -> showMain = true },
                        onForgotPasswordClick = comingSoon,
                        onRegisterClick = comingSoon,
                        onGoogleClick = comingSoon,
                        onFacebookClick = comingSoon
                    )
                }
            }
        }
    }
}
