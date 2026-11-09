package com.example.ch3mxr.data.learning

import androidx.room.withTransaction
import com.example.ch3mxr.core.model.*
import com.example.ch3mxr.data.local.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

@Singleton
class LearningRepository
@Inject
constructor(private val db: ChemXrDatabase, private val legacy: LegacyLearningReader) {
  private val dao
    get() = db.learning()

  val state: Flow<LearningState> =
    flow {
        initialize()
        emitAll(dao.observeMeta().map { db.withTransaction { read() } })
      }
      .flowOn(Dispatchers.IO)

  suspend fun initialize() =
    db.withTransaction {
      if (dao.migrated("learning_v1") == 0) {
        val imported = legacy.read()
        persist(LearningState(), imported)
        dao.meta(LearningMetaEntity(plan = imported.plan))
        dao.mark(MigrationFlagEntity("learning_v1"))
      }
    }

  internal suspend fun <T> update(action: LearningEditor.() -> T): T =
    db.withTransaction {
      initialize()
      val before = read()
      val editor = LearningEditor(before)
      val result = editor.action()
      val after = editor.freeze()
      if (before != after) {
        persist(before, after)
        dao.meta(LearningMetaEntity(plan = after.plan, revision = (dao.meta()?.revision ?: 0) + 1))
      }
      result
    }

  private suspend fun read(): LearningState {
    val links = dao.links()
    val moderation = dao.moderation()
    return LearningState(
      plan = dao.meta()?.plan ?: StudentPlan.FREE,
      purchasedIds = links.filter { it.kind == "purchased" }.map { it.courseId },
      enrolledIds = links.filter { it.kind == "enrolled" }.map { it.courseId },
      cartIds = links.filter { it.kind == "cart" }.map { it.courseId },
      resources = dao.catalogResource().map { it.model() },
      shortcuts = dao.savedShortcut().map { it.model() },
      posts = dao.communityPost().map { it.model() },
      replies = dao.communityReply().map { it.model() },
      classEvents = dao.classroomEvent().map { it.model() },
      classNotes = dao.classroomNote().map { it.model() },
      votes = dao.votes().associate { it.id to it.direction },
      roles = dao.roles().associate { it.courseId to it.role },
      hidden = moderation.filter { it.kind == "hidden" }.map { it.entry },
      reports = moderation.filter { it.kind == "report" }.map { it.entry },
    )
  }

  private suspend fun persist(before: LearningState, after: LearningState) {
    if (before.resources != after.resources) {
      val old = before.resources.mapIndexed { i, v -> v.entity(i) }.associateBy { it.id }
      val next = after.resources.mapIndexed { i, v -> v.entity(i) }.associateBy { it.id }
      dao.deleteCatalogResource((old.keys - next.keys).toList())
      dao.putCatalogResource(next.values.filter { old[it.id] != it })
    }
    if (before.shortcuts != after.shortcuts) {
      val old = before.shortcuts.mapIndexed { i, v -> v.entity(i) }.associateBy { it.id }
      val next = after.shortcuts.mapIndexed { i, v -> v.entity(i) }.associateBy { it.id }
      dao.deleteSavedShortcut((old.keys - next.keys).toList())
      dao.putSavedShortcut(next.values.filter { old[it.id] != it })
    }
    if (before.posts != after.posts) {
      val old = before.posts.mapIndexed { i, v -> v.entity(i) }.associateBy { it.id }
      val next = after.posts.mapIndexed { i, v -> v.entity(i) }.associateBy { it.id }
      dao.deleteCommunityPost((old.keys - next.keys).toList())
      dao.putCommunityPost(next.values.filter { old[it.id] != it })
    }
    if (before.replies != after.replies) {
      val old = before.replies.mapIndexed { i, v -> v.entity(i) }.associateBy { it.id }
      val next = after.replies.mapIndexed { i, v -> v.entity(i) }.associateBy { it.id }
      dao.deleteCommunityReply((old.keys - next.keys).toList())
      dao.putCommunityReply(next.values.filter { old[it.id] != it })
    }
    if (before.classEvents != after.classEvents) {
      val old = before.classEvents.mapIndexed { i, v -> v.entity(i) }.associateBy { it.id }
      val next = after.classEvents.mapIndexed { i, v -> v.entity(i) }.associateBy { it.id }
      dao.deleteClassroomEvent((old.keys - next.keys).toList())
      dao.putClassroomEvent(next.values.filter { old[it.id] != it })
    }
    if (before.classNotes != after.classNotes) {
      val old = before.classNotes.mapIndexed { i, v -> v.entity(i) }.associateBy { it.id }
      val next = after.classNotes.mapIndexed { i, v -> v.entity(i) }.associateBy { it.id }
      dao.deleteClassroomNote((old.keys - next.keys).toList())
      dao.putClassroomNote(next.values.filter { old[it.id] != it })
    }
    if (
      before.purchasedIds != after.purchasedIds ||
        before.enrolledIds != after.enrolledIds ||
        before.cartIds != after.cartIds
    ) {
      dao.clearLinks()
      dao.links(
        listOf(
            "purchased" to after.purchasedIds,
            "enrolled" to after.enrolledIds,
            "cart" to after.cartIds,
          )
          .flatMap { (kind, ids) ->
            ids.distinct().mapIndexed { i, id -> CourseLinkEntity(kind, id, i) }
          }
      )
    }
    if (before.votes != after.votes) {
      dao.deleteVotes((before.votes.keys - after.votes.keys).toList())
      dao.votes(
        after.votes.filter { before.votes[it.key] != it.value }.map { VoteEntity(it.key, it.value) }
      )
    }
    if (before.roles != after.roles) {
      dao.deleteRoles((before.roles.keys - after.roles.keys).toList())
      dao.roles(
        after.roles.filter { before.roles[it.key] != it.value }.map { RoleEntity(it.key, it.value) }
      )
    }
    if (before.hidden != after.hidden || before.reports != after.reports) {
      dao.clearModeration()
      dao.moderation(
        listOf("hidden" to after.hidden, "report" to after.reports).flatMap { (kind, items) ->
          items.mapIndexed { i, item -> ModerationEntity(kind, item, i) }
        }
      )
    }
  }
}
