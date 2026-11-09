package com.example.ch3mxr.feature.main.community

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ch3mxr.core.model.*
import com.example.ch3mxr.feature.community.CommunityFeedScreen
import com.example.ch3mxr.feature.profile.model.DemoStudySpaces
import com.example.ch3mxr.feature.profile.model.StudySpace

@Composable
fun CommunityScreen(
  modifier: Modifier = Modifier,
  space: StudySpace = DemoStudySpaces.available.first(),
  onOpen: (String) -> Unit = {},
) {
  CommunityFeedScreen(space, modifier, onOpen)
}
