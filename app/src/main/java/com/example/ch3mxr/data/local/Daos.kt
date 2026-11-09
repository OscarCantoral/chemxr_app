package com.example.ch3mxr.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LearningDao {
  @Query("SELECT * FROM learning_meta WHERE id=1") fun observeMeta(): Flow<LearningMetaEntity?>

  @Query("SELECT * FROM learning_meta WHERE id=1") suspend fun meta(): LearningMetaEntity?

  @Upsert suspend fun meta(value: LearningMetaEntity)

  @Query("SELECT COUNT(*) FROM migration_flags WHERE name=:name")
  suspend fun migrated(name: String): Int

  @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun mark(value: MigrationFlagEntity)

  @Query("SELECT * FROM resources ORDER BY orderIndex")
  suspend fun catalogResource(): List<CatalogResourceEntity>

  @Upsert suspend fun putCatalogResource(value: List<CatalogResourceEntity>)

  @Query("DELETE FROM resources WHERE id IN (:ids)")
  suspend fun deleteCatalogResource(ids: List<String>)

  @Query("SELECT * FROM shortcuts ORDER BY orderIndex")
  suspend fun savedShortcut(): List<SavedShortcutEntity>

  @Upsert suspend fun putSavedShortcut(value: List<SavedShortcutEntity>)

  @Query("DELETE FROM shortcuts WHERE id IN (:ids)")
  suspend fun deleteSavedShortcut(ids: List<String>)

  @Query("SELECT * FROM posts ORDER BY orderIndex")
  suspend fun communityPost(): List<CommunityPostEntity>

  @Upsert suspend fun putCommunityPost(value: List<CommunityPostEntity>)

  @Query("DELETE FROM posts WHERE id IN (:ids)") suspend fun deleteCommunityPost(ids: List<String>)

  @Query("SELECT * FROM replies ORDER BY orderIndex")
  suspend fun communityReply(): List<CommunityReplyEntity>

  @Upsert suspend fun putCommunityReply(value: List<CommunityReplyEntity>)

  @Query("DELETE FROM replies WHERE id IN (:ids)")
  suspend fun deleteCommunityReply(ids: List<String>)

  @Query("SELECT * FROM class_events ORDER BY orderIndex")
  suspend fun classroomEvent(): List<ClassroomEventEntity>

  @Upsert suspend fun putClassroomEvent(value: List<ClassroomEventEntity>)

  @Query("DELETE FROM class_events WHERE id IN (:ids)")
  suspend fun deleteClassroomEvent(ids: List<String>)

  @Query("SELECT * FROM class_notes ORDER BY orderIndex")
  suspend fun classroomNote(): List<ClassroomNoteEntity>

  @Upsert suspend fun putClassroomNote(value: List<ClassroomNoteEntity>)

  @Query("DELETE FROM class_notes WHERE id IN (:ids)")
  suspend fun deleteClassroomNote(ids: List<String>)

  @Query("SELECT * FROM course_links ORDER BY orderIndex")
  suspend fun links(): List<CourseLinkEntity>

  @Query("DELETE FROM course_links") suspend fun clearLinks()

  @Upsert suspend fun links(values: List<CourseLinkEntity>)

  @Query("SELECT * FROM votes") suspend fun votes(): List<VoteEntity>

  @Query("DELETE FROM votes WHERE id IN (:ids)") suspend fun deleteVotes(ids: List<String>)

  @Upsert suspend fun votes(values: List<VoteEntity>)

  @Query("SELECT * FROM roles") suspend fun roles(): List<RoleEntity>

  @Query("DELETE FROM roles WHERE courseId IN (:ids)") suspend fun deleteRoles(ids: List<String>)

  @Upsert suspend fun roles(values: List<RoleEntity>)

  @Query("SELECT * FROM moderation ORDER BY orderIndex")
  suspend fun moderation(): List<ModerationEntity>

  @Query("DELETE FROM moderation") suspend fun clearModeration()

  @Upsert suspend fun moderation(values: List<ModerationEntity>)
}

@Dao
interface PersonalDao {
  @Query("SELECT * FROM notes WHERE ownerId=:ownerId ORDER BY createdAt DESC,id DESC")
  fun notes(ownerId: String = "demo"): Flow<List<NoteEntity>>

  @Insert suspend fun add(note: NoteEntity)

  @Query("DELETE FROM notes WHERE id=:id AND ownerId=:ownerId")
  suspend fun deleteNote(id: String, ownerId: String = "demo")

  @Query("SELECT * FROM agenda WHERE ownerId=:ownerId ORDER BY start")
  fun agenda(ownerId: String = "demo"): Flow<List<AgendaEntity>>

  @Insert suspend fun add(event: AgendaEntity)

  @Query("DELETE FROM agenda WHERE id=:id AND ownerId=:ownerId")
  suspend fun deleteEvent(id: String, ownerId: String = "demo")
}

@Dao
interface OfflineDao {
  @Query("UPDATE offline_resources SET state='FAILED' WHERE state IN ('DOWNLOADING','QUEUED')")
  suspend fun recoverInterrupted()

  @Query("SELECT * FROM offline_resources WHERE resourceId=:id")
  suspend fun find(id: String): OfflineEntity?

  @Query("SELECT * FROM offline_resources WHERE courseId=:courseId")
  suspend fun byCourse(courseId: String): List<OfflineEntity>

  @Query("SELECT * FROM offline_resources WHERE resourceId=:id")
  fun observe(id: String): Flow<OfflineEntity?>

  @Upsert suspend fun save(item: OfflineEntity)
}
