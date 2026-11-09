package com.example.ch3mxr.data.personal

import android.content.Context
import androidx.room.withTransaction
import com.example.ch3mxr.core.model.*
import com.example.ch3mxr.data.local.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.json.JSONArray

@Singleton
class PersonalRepository
@Inject
constructor(
  private val db: ChemXrDatabase,
  @param:ApplicationContext private val context: Context,
) {
  val notes =
    flow {
        initialize()
        emitAll(
          db.personal().notes().map { rows ->
            rows.map { UserNote(it.id, it.text, it.photoUri, it.createdAt) }
          }
        )
      }
      .flowOn(Dispatchers.IO)
  val agenda =
    flow {
        initialize()
        emitAll(
          db.personal().agenda().map { rows ->
            rows.map {
              AgendaItem(
                it.id,
                it.ownerId,
                it.spaceId,
                it.title,
                it.type,
                it.start,
                it.end,
                it.allDay,
              )
            }
          }
        )
      }
      .flowOn(Dispatchers.IO)

  suspend fun initialize() =
    db.withTransaction {
      if (db.learning().migrated("personal_v1") == 0) {
        val notes =
          JSONArray(context.getSharedPreferences("chemxr_user_notes", 0).getString("notes", "[]"))
        val importedAt = System.currentTimeMillis()
        for (i in 0 until notes.length()) {
          val x = notes.getJSONObject(i)
          val id = UUID.nameUUIDFromBytes("legacy-note:$i:$x".toByteArray()).toString()
          db
            .personal()
            .add(
              NoteEntity(
                id,
                text = x.optString("text"),
                photoUri = x.optString("photo"),
                createdAt = importedAt - i,
              )
            )
        }
        val entries =
          JSONArray(
            context.getSharedPreferences("chemxr_agenda_personal", 0).getString("entries", "[]")
          )
        for (i in 0 until entries.length()) {
          val x = entries.getJSONObject(i)
          val start = LocalDateTime.parse(x.getString("start"))
          val end = LocalDateTime.parse(x.getString("end"))
          require(end.isAfter(start))
          val space = x.getString("owner")
          require(space in setOf("personal", "utp", "senati"))
          db
            .personal()
            .add(
              AgendaEntity(
                UUID.nameUUIDFromBytes("legacy-agenda:$i:$x".toByteArray()).toString(),
                spaceId = space,
                title = x.getString("title"),
                type = x.getString("type"),
                start = start.toString(),
                end = end.toString(),
                allDay = x.optBoolean("allDay"),
              )
            )
        }
        db.learning().mark(MigrationFlagEntity("personal_v1"))
      }
    }

  suspend fun addNote(text: String, photo: String) =
    withContext(Dispatchers.IO) {
      initialize()
      require(text.isNotBlank() || photo.isNotBlank())
      require(photo.isBlank() || photo.startsWith("content://"))
      db
        .personal()
        .add(
          NoteEntity(
            uid(),
            text = text.trim(),
            photoUri = photo,
            createdAt = System.currentTimeMillis(),
          )
        )
    }

  suspend fun deleteNote(id: String) {
    initialize()
    db.personal().deleteNote(id)
  }

  suspend fun addEvent(item: AgendaItem) {
    initialize()
    require(item.spaceId in setOf("personal", "utp", "senati"))
    require(
      item.title.isNotBlank() &&
        LocalDateTime.parse(item.end).isAfter(LocalDateTime.parse(item.start))
    )
    db
      .personal()
      .add(
        AgendaEntity(
          item.id,
          item.ownerId,
          item.spaceId,
          item.title,
          item.type,
          item.start,
          item.end,
          item.allDay,
        )
      )
  }
}
