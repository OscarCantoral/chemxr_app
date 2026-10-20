package com.example.ch3mxr.feature.main.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ch3mxr.core.ui.components.ChemBottomBar
import com.example.ch3mxr.core.ui.components.ChemHomeTopBar

@Composable
fun MainShellScreen() {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.HOME) }

    Scaffold(
        topBar = {
            ChemHomeTopBar()
        },
        bottomBar = {
            ChemBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // SIN CONTENIDO por ahora
            // Luego aquí renderizaremos el contenido según selectedTab
        }
    }
}