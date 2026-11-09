package com.example.ch3mxr.core.model
/** UI-facing values are independent of the Room schema. */
data class UserNote(val id: String, val text: String, val photoUri: String, val createdAt: Long)

data class AgendaItem(
  val id: String,
  val ownerId: String = "demo",
  val spaceId: String,
  val title: String,
  val type: String,
  val start: String,
  val end: String,
  val allDay: Boolean,
)
