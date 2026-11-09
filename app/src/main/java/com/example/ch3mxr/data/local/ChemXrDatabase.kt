package com.example.ch3mxr.data.local

import androidx.room.*

@Database(
  entities =
    [
      CatalogResourceEntity::class,
      SavedShortcutEntity::class,
      CommunityPostEntity::class,
      CommunityReplyEntity::class,
      ClassroomEventEntity::class,
      ClassroomNoteEntity::class,
      CourseLinkEntity::class,
      VoteEntity::class,
      RoleEntity::class,
      ModerationEntity::class,
      LearningMetaEntity::class,
      MigrationFlagEntity::class,
      NoteEntity::class,
      AgendaEntity::class,
      OfflineEntity::class,
    ],
  version = 1,
  exportSchema = true,
)
@TypeConverters(EnumConverters::class)
abstract class ChemXrDatabase : RoomDatabase() {
  abstract fun learning(): LearningDao

  abstract fun personal(): PersonalDao

  abstract fun offline(): OfflineDao
}
