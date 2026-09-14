package com.example.ch3mxr.ui.features.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.ch3mxr.ui.components.ArCoreBanner
import com.example.ch3mxr.ui.components.CourseCard
import com.example.ch3mxr.ui.model.CourseCatalog

@Composable
fun HomeScreen(
    onCourseClick: (String) -> Unit,
    onLibresClick: () -> Unit,
    onGruposClick: () -> Unit,
    onLogoutClick: () -> Unit = {},
    arSupported: Boolean
) {
    val cyanColor = Color(0xFF0ED2F7)
    val darkBg = Color(0xFF020617)

    var selectedTab by remember { mutableStateOf("libres") }

    val cursos = CourseCatalog.all
    var showArBanner by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBg)
            .padding(16.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CURSOS",
                    color = Color.White
                )

                IconButton(onClick = onLogoutClick) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Cerrar sesión",
                        tint = Color.Red.copy(alpha = 0.8f)
                    )
                }
            }


            AnimatedVisibility(
                visible = arSupported && showArBanner
            ) {
                ArCoreBanner(
                    onClose = {
                        showArBanner = false
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Spacer(modifier = Modifier.height(40.dp))

            LazyColumn(

                modifier = Modifier.weight(1f),

                verticalArrangement = Arrangement.spacedBy(16.dp)

            ) {

                items(cursos) { curso ->

                    CourseCard(

                        titulo = curso.title,

                        descripcion = curso.description,

                        imagen = curso.imageRes,

                        onClick = {
                            onCourseClick(curso.id)
                        }

                    )

                }

            }


            // 🔻 FOOTER (alineado correctamente)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // LIBRES
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PUBLICO",
                        color = if (selectedTab == "libres") cyanColor else Color.Gray,
                        modifier = Modifier.clickable {
                            selectedTab = "libres"
                            onLibresClick()
                        }
                    )

                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .width(50.dp)
                            .background(
                                if (selectedTab == "libres") cyanColor else Color.Transparent
                            )
                    )
                }

                // GRUPOS
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PRIVADOS",
                        color = if (selectedTab == "grupos") cyanColor else Color.Gray,
                        modifier = Modifier.clickable {
                            selectedTab = "grupos"
                            onGruposClick()
                        }
                    )

                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .width(50.dp)
                            .background(
                                if (selectedTab == "grupos") cyanColor else Color.Transparent
                            )
                    )
                }
            }
        }
    }
}
