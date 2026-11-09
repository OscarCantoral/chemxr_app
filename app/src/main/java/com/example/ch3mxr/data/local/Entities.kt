package com.example.ch3mxr.data.local

import androidx.room.*
import com.example.ch3mxr.core.model.*

@Entity(tableName = "resources", indices = [Index("spaceId")])
data class CatalogResourceEntity(
  @PrimaryKey val id: String,
  val spaceId: String,
  val title: String,
  val kind: ResourceKind,
  val category: String,
  val author: String,
  val tags: String,
  val license: String,
  val url: String,
  val fileUri: String,
  val mime: String,
  val origin: String,
  val courseId: String,
  val orderIndex: Int,
) {
  fun model() =
    CatalogResource(
      id,
      spaceId,
      title,
      kind,
      category,
      author,
      tags,
      license,
      url,
      fileUri,
      mime,
      origin,
      courseId,
    )
}

fun CatalogResource.entity(order: Int) =
  CatalogResourceEntity(
    id,
    spaceId,
    title,
    kind,
    category,
    author,
    tags,
    license,
    url,
    fileUri,
    mime,
    origin,
    courseId,
    order,
  )

@Entity(tableName = "shortcuts", indices = [Index("spaceId")])
data class SavedShortcutEntity(
  @PrimaryKey val id: String,
  val spaceId: String,
  val title: String,
  val kind: String,
  val route: String,
  val orderIndex: Int,
) {
  fun model() = SavedShortcut(id, spaceId, title, kind, route)
}

fun SavedShortcut.entity(order: Int) = SavedShortcutEntity(id, spaceId, title, kind, route, order)

@Entity(tableName = "posts", indices = [Index("spaceId")])
data class CommunityPostEntity(
  @PrimaryKey val id: String,
  val spaceId: String,
  val author: String,
  val initials: String,
  val text: String,
  val baseScore: Int,
  val resourceId: String,
  val group: String,
  val orderIndex: Int,
) {
  fun model() = CommunityPost(id, spaceId, author, initials, text, baseScore, resourceId, group)
}

fun CommunityPost.entity(order: Int) =
  CommunityPostEntity(id, spaceId, author, initials, text, baseScore, resourceId, group, order)

@Entity(
  tableName = "replies",
  indices = [Index("postId")],
  foreignKeys =
    [
      ForeignKey(
        entity = CommunityPostEntity::class,
        parentColumns = ["id"],
        childColumns = ["postId"],
        onDelete = ForeignKey.CASCADE,
      )
    ],
)
data class CommunityReplyEntity(
  @PrimaryKey val id: String,
  val postId: String,
  val author: String,
  val initials: String,
  val text: String,
  val baseScore: Int,
  val resourceId: String,
  val orderIndex: Int,
) {
  fun model() = CommunityReply(id, postId, author, initials, text, baseScore, resourceId)
}

fun CommunityReply.entity(order: Int) =
  CommunityReplyEntity(id, postId, author, initials, text, baseScore, resourceId, order)

@Entity(tableName = "class_events", indices = [Index("spaceId")])
data class ClassroomEventEntity(
  @PrimaryKey val id: String,
  val courseId: String,
  val spaceId: String,
  val sectionId: String,
  val title: String,
  val type: String,
  val start: String,
  val end: String,
  val authorRole: AcademicRole,
  val orderIndex: Int,
) {
  fun model() =
    ClassroomEvent(id, courseId, spaceId, sectionId, title, type, start, end, authorRole)
}

fun ClassroomEvent.entity(order: Int) =
  ClassroomEventEntity(id, courseId, spaceId, sectionId, title, type, start, end, authorRole, order)

@Entity(tableName = "class_notes", indices = [Index("spaceId")])
data class ClassroomNoteEntity(
  @PrimaryKey val id: String,
  val courseId: String,
  val spaceId: String,
  val sectionId: String,
  val recordingId: String,
  val seconds: Int,
  val text: String,
  val photoUri: String,
  val authorRole: AcademicRole,
  val orderIndex: Int,
) {
  fun model() =
    ClassroomNote(
      id,
      courseId,
      spaceId,
      sectionId,
      recordingId,
      seconds,
      text,
      photoUri,
      authorRole,
    )
}

fun ClassroomNote.entity(order: Int) =
  ClassroomNoteEntity(
    id,
    courseId,
    spaceId,
    sectionId,
    recordingId,
    seconds,
    text,
    photoUri,
    authorRole,
    order,
  )

@Entity(tableName = "course_links", primaryKeys = ["kind", "courseId"])
data class CourseLinkEntity(val kind: String, val courseId: String, val orderIndex: Int)

@Entity(tableName = "votes") data class VoteEntity(@PrimaryKey val id: String, val direction: Int)

@Entity(tableName = "roles")
data class RoleEntity(@PrimaryKey val courseId: String, val role: AcademicRole)

@Entity(tableName = "moderation", primaryKeys = ["kind", "orderIndex"])
data class ModerationEntity(val kind: String, val entry: String, val orderIndex: Int)

@Entity(tableName = "learning_meta")
data class LearningMetaEntity(
  @PrimaryKey val id: Int = 1,
  val plan: StudentPlan = StudentPlan.FREE,
  val revision: Long = 0,
)

@Entity(tableName = "migration_flags") data class MigrationFlagEntity(@PrimaryKey val name: String)

@Entity(tableName = "notes", indices = [Index("ownerId")])
data class NoteEntity(
  @PrimaryKey val id: String,
  val ownerId: String = "demo",
  val text: String,
  val photoUri: String = "",
  val createdAt: Long,
)

@Entity(tableName = "agenda", indices = [Index("ownerId", "spaceId")])
data class AgendaEntity(
  @PrimaryKey val id: String,
  val ownerId: String = "demo",
  val spaceId: String,
  val title: String,
  val type: String,
  val start: String,
  val end: String,
  val allDay: Boolean,
)

@Entity(tableName = "offline_resources", indices = [Index("courseId")])
data class OfflineEntity(
  @PrimaryKey val resourceId: String,
  val courseId: String,
  val title: String,
  val type: String,
  val state: String,
  val localRelativePath: String?,
  val version: String?,
  val sha256: String?,
)

class EnumConverters {
  @TypeConverter fun kind(value: ResourceKind) = value.name

  @TypeConverter fun kind(value: String) = ResourceKind.valueOf(value)

  @TypeConverter fun role(value: AcademicRole) = value.name

  @TypeConverter fun role(value: String) = AcademicRole.valueOf(value)

  @TypeConverter fun plan(value: StudentPlan) = value.name

  @TypeConverter fun plan(value: String) = StudentPlan.valueOf(value)
}
