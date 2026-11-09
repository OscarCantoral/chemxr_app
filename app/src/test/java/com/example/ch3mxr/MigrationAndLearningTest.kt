package com.example.ch3mxr

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.ch3mxr.core.model.*
import com.example.ch3mxr.data.learning.*
import com.example.ch3mxr.data.local.*
import com.example.ch3mxr.data.personal.PersonalRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class MigrationAndLearningTest {
  private lateinit var context: Context
  private lateinit var db: ChemXrDatabase

  @Before
  fun setup() {
    context = ApplicationProvider.getApplicationContext()
    for (name in
      listOf("chemxr_user_notes", "chemxr_agenda_personal", "chemxr_learning_v1")) context
      .getSharedPreferences(name, 0)
      .edit()
      .clear()
      .commit()
    db = Room.inMemoryDatabaseBuilder(context, ChemXrDatabase::class.java).build()
  }

  @After
  fun close() {
    db.close()
  }

  @Test
  fun notesImportPreservesLegitimateDuplicatesAndRunsOnce() = runBlocking {
    context
      .getSharedPreferences("chemxr_user_notes", 0)
      .edit()
      .putString(
        "notes",
        """[{"text":"same","photo":"content://photos/1"},{"text":"same","photo":"content://photos/1"}]""",
      )
      .commit()
    val repo = PersonalRepository(db, context)
    repo.initialize()
    repo.initialize()
    val notes = db.personal().notes().first()
    assertEquals(2, notes.size)
    assertEquals(2, notes.map { it.id }.distinct().size)
    assertTrue(notes.all { it.text == "same" && it.photoUri == "content://photos/1" })
    assertNotNull(context.getSharedPreferences("chemxr_user_notes", 0).getString("notes", null))
  }

  @Test
  fun invalidAgendaRollsBackNotesAndCanBeRetried() = runBlocking {
    context
      .getSharedPreferences("chemxr_user_notes", 0)
      .edit()
      .putString("notes", """[{"text":"preserve me"}]""")
      .commit()
    context
      .getSharedPreferences("chemxr_agenda_personal", 0)
      .edit()
      .putString("entries", "broken JSON")
      .commit()
    val repo = PersonalRepository(db, context)
    try {
      repo.initialize()
      fail("Import must fail")
    } catch (expected: org.json.JSONException) {}
    assertEquals(0, db.learning().migrated("personal_v1"))
    assertTrue(db.personal().notes().first().isEmpty())
    context
      .getSharedPreferences("chemxr_agenda_personal", 0)
      .edit()
      .putString("entries", "[]")
      .commit()
    repo.initialize()
    assertEquals("preserve me", db.personal().notes().first().single().text)
  }

  @Test
  fun checkoutPersistsAtomicallyAndCannotBuyTwice() = runBlocking {
    val repo = LearningRepository(db, LegacyLearningReader(context))
    repo.initialize()
    assertTrue(repo.update { checkout() })
    val result = repo.state.first()
    assertEquals(2, result.purchasedIds.size)
    assertTrue(result.cartIds.isEmpty())
    assertTrue(result.purchasedIds.all { it in result.enrolledIds })
    assertFalse(repo.update { checkout() })
    repo.initialize()
    assertEquals(result, repo.state.first())
  }

  @Test
  fun wrongSpaceResourceCannotBeAttached() = runBlocking {
    val repo = LearningRepository(db, LegacyLearningReader(context))
    repo.initialize()
    assertFalse(repo.update { publish("utp", "", "manual_quimica") })
    assertFalse(repo.update { publish("unknown", "text", "") })
  }

  @Test
  fun classroomRoleAndTimestampAreRevalidatedInsideTransaction() = runBlocking {
    val repo = LearningRepository(db, LegacyLearningReader(context))
    repo.initialize()
    val course = ClassroomCatalog.courses.first()
    assertFalse(repo.update { addClassNote(course, "${course.id}_session10", 10, "note", "") })
    repo.update { setDemoRole(course.id, AcademicRole.DELEGATE) }
    assertFalse(repo.update { addClassNote(course, "${course.id}_session10", 3301, "note", "") })
    assertTrue(
      repo.update {
        addClassNote(course, "${course.id}_session10", 15, "note", "content://photo/1")
      }
    )
    val result = repo.state.first()
    val note = result.classNotes.single()
    assertEquals(course.sectionId, note.sectionId)
    assertTrue(result.resources.any { it.id == "photo_${note.id}" && it.spaceId == course.spaceId })
  }

  @Test
  fun failedOperationDoesNotPersistPartialState() = runBlocking {
    val repo = LearningRepository(db, LegacyLearningReader(context))
    repo.initialize()
    val before = repo.state.first()
    try {
      repo.update {
        setPlan(StudentPlan.PRO)
        throw IllegalStateException("interruption")
      }
      fail("Must fail")
    } catch (expected: IllegalStateException) {}
    assertEquals(before, repo.state.first())
  }

  @Test
  fun malformedLegacyLearningIsNotOverwritten() = runBlocking {
    context
      .getSharedPreferences("chemxr_learning_v1", 0)
      .edit()
      .putString("state", "invalid")
      .commit()
    val repo = LearningRepository(db, LegacyLearningReader(context))
    try {
      repo.initialize()
      fail("Must fail")
    } catch (expected: org.json.JSONException) {}
    assertEquals(0, db.learning().migrated("learning_v1"))
    assertEquals(
      "invalid",
      context.getSharedPreferences("chemxr_learning_v1", 0).getString("state", null),
    )
  }
}
