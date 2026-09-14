package com.example.ch3mxr.ui.features.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ch3mxr.ui.components.PdfComponent
import com.example.ch3mxr.ui.model.Course
import com.example.ch3mxr.ui.model.CourseModule

@Composable
fun CourseScreen(
    course: Course,
    onBack: () -> Unit,
    onArClick: () -> Unit
) {

    val darkBg = Color(0xFF020617)
    val cyan = Color(0xFF0ED2F7)
    val black = Color(0xFF050505)
    val white = Color(0xFFF8FAFC)

    val pdfRed = Color(0xFFDC3129)
    var isOpened: Boolean by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBg)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 24.dp,
                    bottom = 16.dp
                )
        ) {

            Text(
                text = course.title,
                color = Color.White,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = course.tagline,
                color = Color.White.copy(alpha = 0.55f),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            course.modules.chunked(2).forEachIndexed { rowIndex, rowModules ->
                if (rowIndex > 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (rowIndex == 0) 180.dp else 155.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowModules.forEachIndexed { colIndex, module ->
                        val style =
                            tileStyle(rowIndex * 2 + colIndex, cyan, black, white)
                        ModuleTile(
                            number = module.number,
                            title = module.title,
                            subtitle = module.subtitle,
                            backgroundColor = style.backgroundColor,
                            contentColor = style.contentColor,
                            borderColor = style.borderColor,
                            modifier = Modifier
                                .weight(style.weight)
                                .fillMaxHeight(),
                            onClick = {}
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
                    .clickable {
                        isOpened = true
                    },
                shape = RoundedCornerShape(20.dp),
                color = pdfRed,
                shadowElevation = 4.dp
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White
                    ) {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = "PDF",
                                color = pdfRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "MANUAL",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Material de estudio del curso",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 11.sp
                        )
                    }

                    Text(
                        text = "→",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF0B1220),
                border = BorderStroke(
                    1.dp,
                    Color.White.copy(alpha = 0.10f)
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column {

                        Text(
                            text = "PROGRESO DEL CURSO",
                            color = Color.White.copy(alpha = 0.55f),
                            fontSize = 10.sp
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "${course.modules.size} módulos disponibles",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = "●",
                        color = cyan,
                        fontSize = 28.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .clickable {
                        onBack()
                    },
                shape = RoundedCornerShape(18.dp),
                color = cyan
            ) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "←  VOLVER",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        PdfComponent(
            isOpened = isOpened,
            title = course.title,
            pdfAsset = course.manualAsset,
            onClose = { isOpened = false }
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .clickable {
                    onArClick()
                },
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF0B1220),
            border = BorderStroke(
                1.dp,
                cyan.copy(alpha = 0.6f)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "◉  REALIDAD AUMENTADA",
                    color = cyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private data class TileStyle(
    val backgroundColor: Color,
    val contentColor: Color,
    val borderColor: Color,
    val weight: Float
)

private fun tileStyle(index: Int, cyan: Color, black: Color, white: Color): TileStyle =
    when (index % 4) {
        0 -> TileStyle(black, Color.White, Color.White.copy(alpha = 0.25f), 1.45f)
        1 -> TileStyle(cyan, Color.Black, cyan, 0.85f)
        2 -> TileStyle(white, Color.Black, white, 0.90f)
        else -> TileStyle(black, Color.White, cyan, 1.40f)
    }

@Composable
private fun ModuleTile(
    number: String,
    title: String,
    subtitle: String,
    backgroundColor: Color,
    contentColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Surface(
        modifier = modifier
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(22.dp),
        color = backgroundColor,
        border = BorderStroke(
            1.dp,
            borderColor
        ),
        shadowElevation = 4.dp
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = number,
                color = contentColor.copy(alpha = 0.50f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Column {

                Text(
                    text = title,
                    color = contentColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = subtitle,
                    color = contentColor.copy(alpha = 0.65f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            Text(
                text = "→",
                color = contentColor,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
