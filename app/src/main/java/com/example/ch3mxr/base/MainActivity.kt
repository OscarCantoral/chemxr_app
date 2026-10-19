package com.example.ch3mxr.base

import android.os.Bundle
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.example.ch3mxr.core.ui.theme.ChemXRTheme
import com.example.ch3mxr.core.ui.theme.ThemeMode
import com.example.ch3mxr.feature.auth.login.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // El splash usa Theme.Ch3mxr; despues cambiamos al tema normal.
        setTheme(com.example.ch3mxr.R.style.Theme_Ch3mxr_Main)
        super.onCreate(savedInstanceState)
        // Dibuja el fondo Compose tambien detras de las barras del sistema.
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }

        setContent {
            // Cambiar a ThemeMode.LIGHT o ThemeMode.SYSTEM para probar otros temas.
            ChemXRTheme(mode = ThemeMode.DARK) {
                val context = LocalContext.current
                val pending = {
                    Toast.makeText(context, "Función pendiente: prototipo visual", Toast.LENGTH_SHORT).show()
                }

                LoginScreen(
                    onLoginClick = { _, _, _ -> pending() },
                    onForgotPasswordClick = pending,
                    onRegisterClick = pending,
                    onGoogleClick = pending,
                    onFacebookClick = pending
                )
            }
        }
    }
}
