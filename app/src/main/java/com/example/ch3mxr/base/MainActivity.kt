package com.example.ch3mxr.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.ch3mxr.ui.features.App
import com.example.ch3mxr.ui.theme.Ch3mxrTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.ar.core.ArCoreApk
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Ch3mxrTheme {
                App()
            }
        }
    }
}