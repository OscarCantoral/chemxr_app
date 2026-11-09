package com.example.ch3mxr.core.model
/** One local DEMO identity. No field grants server-side permissions or purchases. */
data class LearningState(
  val plan: StudentPlan = StudentPlan.FREE,
  val purchasedIds: List<String> = emptyList(),
  val enrolledIds: List<String> = emptyList(),
  val cartIds: List<String> = emptyList(),
  val resources: List<CatalogResource> = emptyList(),
  val shortcuts: List<SavedShortcut> = emptyList(),
  val posts: List<CommunityPost> = emptyList(),
  val replies: List<CommunityReply> = emptyList(),
  val votes: Map<String, Int> = emptyMap(),
  val hidden: List<String> = emptyList(),
  val reports: List<String> = emptyList(),
  val roles: Map<String, AcademicRole> = emptyMap(),
  val classEvents: List<ClassroomEvent> = emptyList(),
  val classNotes: List<ClassroomNote> = emptyList(),
)
