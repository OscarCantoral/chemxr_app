package com.example.ch3mxr.feature.learning

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.*
import com.example.ch3mxr.core.model.*
import com.example.ch3mxr.data.learning.*
import com.example.ch3mxr.feature.main.model.DemoCourse
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

interface LearningActions {
  fun setPlan(value: StudentPlan)

  fun addToCart(id: String)

  fun removeFromCart(id: String)

  fun checkout(onResult: (Boolean) -> Unit)

  fun enroll(courseId: String, onResult: (Boolean) -> Unit)

  fun toggleShortcut(item: SavedShortcut)

  fun vote(key: String, direction: Int)

  fun hide(key: String)

  fun showHidden()

  fun report(key: String, reason: String)

  fun registerResource(item: CatalogResource, onResult: (Boolean) -> Unit)

  fun publish(spaceId: String, text: String, resourceId: String, onResult: (Boolean) -> Unit)

  fun reply(postId: String, text: String, resourceId: String, onResult: (Boolean) -> Unit)

  fun ensureForum(course: ClassroomCourse)

  fun setDemoRole(courseId: String, value: AcademicRole)

  fun addClassEvent(
    course: ClassroomCourse,
    title: String,
    type: String,
    start: LocalDateTime,
    end: LocalDateTime,
    onResult: (Boolean) -> Unit,
  )

  fun addClassNote(
    course: ClassroomCourse,
    recordingId: String,
    seconds: Int,
    text: String,
    photoUri: String,
    onResult: (Boolean) -> Unit,
  )
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class LearningViewModel @Inject constructor(private val repo: LearningRepository) :
  ViewModel(), LearningActions {
  private val reload = MutableStateFlow(0)
  val state =
    reload
      .flatMapLatest {
        repo.state
          .map<LearningState, LoadState<LearningState>> { LoadState.Ready(it) }
          .catch {
            emit(
              LoadState.Failed(
                "No se pudieron recuperar tus datos locales. Se conservaron los archivos originales."
              )
            )
          }
      }
      .stateIn(viewModelScope, SharingStarted.Eagerly, LoadState.Loading)
  private val _errors = MutableSharedFlow<String>(extraBufferCapacity = 4)
  val errors = _errors.asSharedFlow()

  fun retry() {
    reload.value++
  }

  private fun act(result: (Boolean) -> Unit = {}, block: suspend () -> Boolean) {
    viewModelScope.launch {
      try {
        result(block())
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        _errors.emit("No se pudo guardar la operación. Tus cambios anteriores se conservan.")
        result(false)
      }
    }
  }

  override fun setPlan(value: StudentPlan) {
    act() {
      repo.update {
        setPlan(value)
        true
      }
    }
  }

  override fun addToCart(id: String) {
    act() {
      repo.update {
        addToCart(id)
        true
      }
    }
  }

  override fun removeFromCart(id: String) {
    act() {
      repo.update {
        removeFromCart(id)
        true
      }
    }
  }

  override fun checkout(onResult: (Boolean) -> Unit) {
    act(onResult) { repo.update { checkout() } }
  }

  override fun enroll(courseId: String, onResult: (Boolean) -> Unit) {
    act(onResult) { repo.update { enroll(courseId) } }
  }

  override fun toggleShortcut(item: SavedShortcut) {
    act() {
      repo.update {
        toggleShortcut(item)
        true
      }
    }
  }

  override fun vote(key: String, direction: Int) {
    act() {
      repo.update {
        vote(key, direction)
        true
      }
    }
  }

  override fun hide(key: String) {
    act() {
      repo.update {
        hide(key)
        true
      }
    }
  }

  override fun showHidden() {
    act() {
      repo.update {
        showHidden()
        true
      }
    }
  }

  override fun report(key: String, reason: String) {
    act() {
      repo.update {
        report(key, reason)
        true
      }
    }
  }

  override fun registerResource(item: CatalogResource, onResult: (Boolean) -> Unit) {
    act(onResult) { repo.update { registerResource(item) } }
  }

  override fun publish(
    spaceId: String,
    text: String,
    resourceId: String,
    onResult: (Boolean) -> Unit,
  ) {
    act(onResult) { repo.update { publish(spaceId, text, resourceId) } }
  }

  override fun reply(
    postId: String,
    text: String,
    resourceId: String,
    onResult: (Boolean) -> Unit,
  ) {
    act(onResult) { repo.update { reply(postId, text, resourceId) } }
  }

  override fun ensureForum(course: ClassroomCourse) {
    act() {
      repo.update {
        ensureForum(course)
        true
      }
    }
  }

  override fun setDemoRole(courseId: String, value: AcademicRole) {
    act() {
      repo.update {
        setDemoRole(courseId, value)
        true
      }
    }
  }

  override fun addClassEvent(
    course: ClassroomCourse,
    title: String,
    type: String,
    start: LocalDateTime,
    end: LocalDateTime,
    onResult: (Boolean) -> Unit,
  ) {
    act(onResult) { repo.update { addClassEvent(course, title, type, start, end) } }
  }

  override fun addClassNote(
    course: ClassroomCourse,
    recordingId: String,
    seconds: Int,
    text: String,
    photoUri: String,
    onResult: (Boolean) -> Unit,
  ) {
    act(onResult) { repo.update { addClassNote(course, recordingId, seconds, text, photoUri) } }
  }
}

class LearningUiController(val state: LearningState, actions: LearningActions) :
  LearningActions by actions {
  val plan
    get() = state.plan

  val isPro
    get() = plan == StudentPlan.PRO

  val tutorAvailable
    get() = isPro

  fun adsAllowed(spaceId: String) = spaceId == "personal" && !isPro

  val purchasedIds
    get() = state.purchasedIds

  val enrolledIds
    get() = state.enrolledIds

  val cartIds
    get() = state.cartIds

  val resources
    get() = state.resources

  val shortcuts
    get() = state.shortcuts

  val posts
    get() = state.posts

  val replies
    get() = state.replies

  val votes
    get() = state.votes

  val hidden
    get() = state.hidden

  val reports
    get() = state.reports

  val classEvents
    get() = state.classEvents

  val classNotes
    get() = state.classNotes

  fun canStudy(course: DemoCourse) =
    LearningRules.canStudy(course.access, plan, course.id in purchasedIds)

  fun myCourses() = LearningCatalog.all.filter { it.id in enrolledIds || it.id in purchasedIds }

  fun isSaved(id: String) = shortcuts.any { it.id == id }

  fun resource(id: String, spaceId: String) =
    resources.firstOrNull { it.id == id && it.spaceId == spaceId }

  fun role(courseId: String) = state.roles[courseId] ?: AcademicRole.STUDENT

  fun linksRegistered(spaceId: String, text: String) =
    LearningRules.webLinks(text).all { url ->
      resources.any { it.spaceId == spaceId && it.url.trimEnd('/') == url.trimEnd('/') }
    }
}

val LocalLearning =
  staticCompositionLocalOf<LearningUiController> { error("Learning is provided by AppRoot") }
