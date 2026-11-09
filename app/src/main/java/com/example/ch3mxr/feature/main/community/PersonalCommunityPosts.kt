package com.example.ch3mxr.feature.main.community

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ch3mxr.core.model.*
import com.example.ch3mxr.feature.community.DiscussionScreen
import com.example.ch3mxr.feature.profile.model.DemoStudySpaces

/** Compatibilidad con las rutas de la versión previa. */
@Composable
fun PersonalPostCommentsScreen(postId: String, modifier: Modifier = Modifier) {
  DiscussionScreen(postId, DemoStudySpaces.available.first(), modifier, {})
}
