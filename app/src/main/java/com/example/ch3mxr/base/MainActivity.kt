package com.example.ch3mxr.base

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ch3mxr.R
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
            ChemXRTheme(mode = ThemeMode.DARK) {
                // SOLO navegación simulada, sin validar credenciales ni crear sesión.
                var showMain by rememberSaveable { mutableStateOf(false) }
                val context = LocalContext.current
                val pending = {
                    Toast.makeText(context, "Disponible en una etapa posterior", Toast.LENGTH_SHORT).show()
                }

                // Para la previsualización, volver desde los tabs lleva al login.
                BackHandler(enabled = showMain) { showMain = false }

                if (showMain) {
                    MainShellScreen()
                } else {
                    LoginScreen(
                        onLoginClick = { _, _, _ -> showMain = true },
                        onForgotPasswordClick = pending,
                        onRegisterClick = pending,
                        onGoogleClick = pending,
                        onFacebookClick = pending
                    )
                }
            }
        }
    }
}
