package com.example.ch3mxr.data.demo

import com.example.ch3mxr.core.model.*

object DemoCourses {
  val purchased =
    setOf(
      "chemistry_ar",
      "kotlin_basics",
      "physics_motion",
      "modeling_3d",
      "excel_data",
      "music_production",
    )
  val free = setOf("adrom_chemistry", "adrom_algorithm", "adrom_music", "adrom_strategy")
  val pro = setOf("adrom_xr_pro", "adrom_molecules_pro", "adrom_stem_pro")
  val initialEnrollment = listOf("adrom_chemistry", "adrom_music")
  val initialSelection = listOf("chemistry_ar", "kotlin_basics")

  fun access(id: String): CourseAccess? =
    when (id) {
      in free -> CourseAccess.FREE
      in pro -> CourseAccess.PRO
      in purchased -> CourseAccess.PURCHASE
      else -> null
    }
}
