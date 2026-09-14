package com.example.ch3mxr.ui.features

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.ch3mxr.ui.routes.Graph
import com.example.ch3mxr.ui.routes.appGraph
import com.example.ch3mxr.ui.routes.authGraph
import com.example.ch3mxr.ui.routes.mainGraph
import com.google.ar.core.ArCoreApk

@Composable
fun App() {
    val navController = rememberNavController()
    val sessionViewModel: SessionViewModel = hiltViewModel()
    var arSupported by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        ArCoreApk.getInstance().checkAvailabilityAsync(context) { availability ->
            arSupported = availability.isSupported
        }
    }

    NavHost(navController, startDestination = Graph.App) {
        appGraph(navController, sessionViewModel)
        authGraph(navController, sessionViewModel)
        mainGraph(navController, sessionViewModel, arSupported = arSupported)
    }
}