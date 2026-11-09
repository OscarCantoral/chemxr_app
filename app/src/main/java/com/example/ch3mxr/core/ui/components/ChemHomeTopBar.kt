package com.example.ch3mxr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ch3mxr.core.model.*
import com.example.ch3mxr.core.ui.theme.ChemDimens
import com.example.ch3mxr.core.ui.theme.ChemShapes
import com.example.ch3mxr.feature.profile.model.StudySpace

@Composable
fun ChemHomeTopBar(
  searchPlaceholder: String,
  onProfileClick: () -> Unit = {},
  onSearchClick: () -> Unit = {},
  onAiClick: () -> Unit = {},
  onNotificationsClick: () -> Unit,
  activeSpace: StudySpace? = null,
  onSpaceClick: () -> Unit = {},
  onSelectionClick: () -> Unit = {},
  selectionCount: Int = 0,
  showTutor: Boolean = false,
) {
  val c = MaterialTheme.colorScheme
  Column(Modifier.fillMaxWidth().background(c.background).statusBarsPadding()) {
    Row(
      Modifier.fillMaxWidth()
        .height(ChemDimens.TopBarHeight)
        .padding(horizontal = ChemDimens.TopBarHorizontalPadding),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
      ChemProfileAvatar(onClick = onProfileClick)
      Spacer(Modifier.weight(1f))
      ChemIconAction(
        icon = Icons.Outlined.Search,
        contentDescription = "Buscar. $searchPlaceholder",
        onClick = onSearchClick,
        modifier = Modifier.size(ChemDimens.TopBarActionSize),
        tint = c.onBackground,
      )
      if (showTutor) ChemAiButton(onClick = onAiClick)
      if (activeSpace?.id == "personal") {
        Box(
          Modifier.size(ChemDimens.TopBarActionSize)
            .clickable(role = Role.Button, onClick = onSelectionClick),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            Icons.Outlined.ShoppingCart,
            "Mi selección, $selectionCount ${if(selectionCount==1) "curso" else "cursos"}",
            Modifier.size(23.dp),
            tint = c.onBackground,
          )
          if (selectionCount > 0) {
            Box(
              Modifier.align(Alignment.TopEnd)
                .padding(top = 2.dp, end = 2.dp)
                .size(16.dp)
                .background(c.primary, ChemShapes.Component)
                .clearAndSetSemantics {},
              contentAlignment = Alignment.Center,
            ) {
              Text(
                if (selectionCount > 9) "9+" else selectionCount.toString(),
                color = c.onPrimary,
                fontSize = 9.sp,
                lineHeight = 11.sp,
                fontWeight = FontWeight.Bold,
              )
            }
          }
        }
      }
      Box(
        Modifier.size(ChemDimens.TopBarActionSize)
          .clickable(role = Role.Button, onClick = onNotificationsClick),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          Icons.Outlined.Notifications,
          "Notificaciones",
          Modifier.size(23.dp),
          tint = c.onBackground,
        )
        Box(
          Modifier.align(Alignment.TopEnd)
            .padding(top = 8.dp, end = 10.dp)
            .size(5.dp)
            .background(c.primary, ChemShapes.Component)
        )
      }
    }
    HorizontalDivider(color = c.outline.copy(alpha = .30f), thickness = 1.dp)
  }
}
