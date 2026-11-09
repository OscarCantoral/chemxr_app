package com.example.ch3mxr.feature.experience

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.R
import com.example.ch3mxr.core.model.*
import com.example.ch3mxr.core.ui.components.ChemPanel
import com.example.ch3mxr.feature.main.components.MainPage
import com.example.ch3mxr.feature.profile.model.StudySpace

/** Valores de maqueta. Más adelante vendrán de una API institucional autorizada. */
data class DigitalStudentPreview(
  val fullName: String = "Oscar Cantoral",
  val documentNumber: String = "70029836",
  @param:DrawableRes val photoRes: Int = R.drawable.foto,
)

/** Dimensiones centralizadas del carnet; todo recto excepto la fotografía. */
private object DigitalCardSizes {
  val InstitutionLogo = 76.dp
  val StudentPhoto = 114.dp
  val SectionSpacing = 20.dp
  val QrMaxWidth = 232.dp
}

/** Pantalla utilizada por ExperiencePageScreen cuando route == "studentCard". */
@Composable
fun StudentCardPage(space: StudySpace, period: String, modifier: Modifier = Modifier) {
  MainPage(modifier) { InstitutionalDigitalCard(space = space, period = period) }
}

/** Carnet reutilizable para UTP, SENATI y futuras entidades académicas. */
@Composable
fun InstitutionalDigitalCard(
  space: StudySpace,
  period: String,
  modifier: Modifier = Modifier,
  student: DigitalStudentPreview = DigitalStudentPreview(),
) {
  val colors = MaterialTheme.colorScheme

  ChemPanel(modifier = modifier) {
    // 1. Encabezado institucional: logo grande + nombre + subtítulo.
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      SpaceMark(space = space, size = DigitalCardSizes.InstitutionLogo)

      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(
          text = space.name,
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.ExtraBold,
          color = colors.onSurface,
        )
        Text(
          text = "Carnet Institucional Digital",
          style = MaterialTheme.typography.bodyMedium,
          color = colors.onSurfaceVariant,
        )
      }
    }

    Spacer(Modifier.height(DigitalCardSizes.SectionSpacing))

    // 2. Fotografía y datos del estudiante.
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(13.dp),
    ) {
      Image(
        painter = painterResource(student.photoRes),
        contentDescription = "Fotografía de demostración del estudiante",
        modifier =
          Modifier.size(DigitalCardSizes.StudentPhoto)
            .clip(CircleShape)
            .background(colors.surfaceVariant, CircleShape)
            .border(2.dp, colors.outline, CircleShape),
        contentScale = ContentScale.Crop,
      )

      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = student.fullName,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.ExtraBold,
          color = colors.onSurface,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = space.studentCode.ifBlank { "CÓDIGO-DEMO" },
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.SemiBold,
          color = colors.onSurface,
        )
        Text(
          text = "DNI: ${student.documentNumber}",
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.SemiBold,
          color = colors.onSurface,
        )
      }
    }

    Spacer(Modifier.height(DigitalCardSizes.SectionSpacing))

    // 3. Carrera a todo el ancho.
    StudentCardField(label = "Carrera", value = space.program.ifBlank { "Sin programa asignado" })

    Spacer(Modifier.height(10.dp))

    // 4. Sede y periodo en dos columnas.
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
      StudentCardField(
        label = "Sede",
        value = space.campus.ifBlank { "Sin sede" },
        modifier = Modifier.weight(1f),
      )
      StudentCardField(
        label = "Periodo",
        value = period.ifBlank { "Sin periodo" },
        modifier = Modifier.weight(1f),
      )
    }

    Spacer(Modifier.height(15.dp))

    // 5. QR de demostración. Contiene solo texto ficticio, NO acredita identidad.
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
      Image(
        painter = painterResource(R.drawable.qr_carnet_demo),
        contentDescription = "Código QR de demostración, sin validez institucional",
        modifier =
          Modifier.fillMaxWidth(.88f).widthIn(max = DigitalCardSizes.QrMaxWidth).aspectRatio(1f),
        contentScale = ContentScale.Fit,
      )
    }

    Spacer(Modifier.height(10.dp))
  }
}

/** Mismo estilo de etiqueta/valor para todas las instituciones. */
@Composable
private fun StudentCardField(label: String, value: String, modifier: Modifier = Modifier) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurface,
      fontWeight = FontWeight.SemiBold,
    )
  }
}
