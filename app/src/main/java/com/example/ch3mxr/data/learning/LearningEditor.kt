package com.example.ch3mxr.data.learning

import com.example.ch3mxr.core.model.*
import com.example.ch3mxr.data.demo.DemoCourses
import java.time.LocalDateTime

internal class LearningEditor(initial: LearningState) {
  private var currentPlan = initial.plan
  val plan
    get() = currentPlan

  val purchasedIds = initial.purchasedIds.toMutableList()
  val enrolledIds = initial.enrolledIds.toMutableList()
  val cartIds = initial.cartIds.toMutableList()
  val resources = initial.resources.toMutableList()
  val shortcuts = initial.shortcuts.toMutableList()
  val posts = initial.posts.toMutableList()
  val replies = initial.replies.toMutableList()
  val hidden = initial.hidden.toMutableList()
  val reports = initial.reports.toMutableList()
  val classEvents = initial.classEvents.toMutableList()
  val classNotes = initial.classNotes.toMutableList()
  val votes = initial.votes.toMutableMap()
  val roles = initial.roles.toMutableMap()

  fun setPlan(value: StudentPlan) {
    currentPlan = value
  }

  fun canStudy(id: String) =
    DemoCourses.access(id)?.let { LearningRules.canStudy(it, plan, id in purchasedIds) } ?: false

  fun addToCart(id: String) {
    if (id in DemoCourses.purchased && id !in purchasedIds && id !in cartIds) {
      cartIds.add(id)
    }
  }

  fun removeFromCart(id: String) {
    cartIds.remove(id)
  }

  fun checkout(): Boolean {
    val valid = cartIds.filter { it in DemoCourses.purchased && it !in purchasedIds }
    if (valid.isEmpty()) return false
    purchasedIds.addAll(valid)
    enrolledIds.addAll(valid.filter { it !in enrolledIds })
    cartIds.clear()
    return true
  }

  fun enroll(courseId: String): Boolean {
    if (!canStudy(courseId)) return false
    if (courseId !in enrolledIds) enrolledIds.add(courseId)
    return true
  }

  fun isSaved(id: String) = shortcuts.any { it.id == id }

  fun toggleShortcut(item: SavedShortcut) {
    if (isSaved(item.id)) shortcuts.removeAll { it.id == item.id } else shortcuts.add(0, item)
  }

  fun vote(key: String, direction: Int) {
    votes[key] = LearningRules.nextVote(votes[key] ?: 0, direction)
  }

  fun hide(key: String) {
    if (key !in hidden) hidden.add(key)
  }

  fun showHidden() {
    hidden.clear()
  }

  fun report(key: String, reason: String) {
    reports.add("$key · $reason")
  }

  fun resource(id: String, spaceId: String) =
    resources.firstOrNull { it.id == id && it.spaceId == spaceId }

  fun registerResource(item: CatalogResource): Boolean {
    if (item.spaceId !in listOf("personal", "utp", "senati") || item.title.isBlank()) return false
    if (!isWebUrl(item.url) && !item.fileUri.startsWith("content://")) return false
    if (resources.any { it.id == item.id }) return false
    resources.add(0, item)
    return true
  }

  fun linksRegistered(spaceId: String, text: String): Boolean =
    LearningRules.webLinks(text).all { url ->
      resources.any { it.spaceId == spaceId && it.url.trimEnd('/') == url.trimEnd('/') }
    }

  fun publish(spaceId: String, text: String, resourceId: String = ""): Boolean {
    if (
      spaceId !in setOf("personal", "utp", "senati") ||
        (text.isBlank() && resourceId.isBlank()) ||
        !linksRegistered(spaceId, text)
    )
      return false
    if (resourceId.isNotBlank() && resource(resourceId, spaceId) == null) return false
    posts.add(
      0,
      CommunityPost(
        uid(),
        spaceId,
        "Oscar Cantoral",
        "OC",
        text.trim(),
        resourceId = resourceId,
        group =
          if (spaceId == "personal") "Curiosos por la ciencia"
          else "Comunidad ${spaceId.uppercase()}",
      ),
    )
    return true
  }

  fun reply(postId: String, text: String, resourceId: String = ""): Boolean {
    val post = posts.firstOrNull { it.id == postId } ?: return false
    if ((text.isBlank() && resourceId.isBlank()) || !linksRegistered(post.spaceId, text))
      return false
    if (resourceId.isNotBlank() && resource(resourceId, post.spaceId) == null) return false
    replies.add(
      CommunityReply(uid(), postId, "Oscar Cantoral", "OC", text.trim(), resourceId = resourceId)
    )
    return true
  }

  fun ensureForum(course: ClassroomCourse): String {
    val id = "forum_${course.id}"
    if (posts.none { it.id == id }) {
      posts.add(
        CommunityPost(
          id,
          course.spaceId,
          course.teacher,
          "DC",
          "Comparte tus preguntas y explica qué parte del curso necesitas reforzar.",
          group = "Foro · ${course.title}",
        )
      )
      replies.add(
        CommunityReply(
          "${id}_welcome",
          id,
          "Delegado del salón",
          "DS",
          "Organicemos las preguntas y citemos nuestras fuentes.",
          2,
        )
      )
    }
    return id
  }

  fun role(courseId: String): AcademicRole = roles[courseId] ?: AcademicRole.STUDENT

  /** Selector EXCLUSIVO de demostración; una API real asignará el rol, jamás el cliente. */
  fun setDemoRole(courseId: String, value: AcademicRole) {
    if (ClassroomCatalog.find(courseId) != null) {
      roles[courseId] = value
    }
  }

  fun addClassEvent(
    course: ClassroomCourse,
    title: String,
    type: String,
    start: LocalDateTime,
    end: LocalDateTime,
  ): Boolean {
    val granted = role(course.id)
    if (!LearningRules.canPublishClass(granted) || title.isBlank() || !end.isAfter(start))
      return false
    if (ClassroomCatalog.find(course.id) != course) return false
    classEvents.add(
      ClassroomEvent(
        uid(),
        course.id,
        course.spaceId,
        course.sectionId,
        title.trim(),
        type,
        start.toString(),
        end.toString(),
        granted,
      )
    )
    return true
  }

  fun addClassNote(
    course: ClassroomCourse,
    recordingId: String,
    seconds: Int,
    text: String,
    photoUri: String,
  ): Boolean {
    val granted = role(course.id)
    if (
      !LearningRules.canPublishClass(granted) ||
        seconds !in 0..3300 ||
        (text.isBlank() && photoUri.isBlank())
    )
      return false
    if (ClassroomCatalog.find(course.id) != course || recordingId != "${course.id}_session10")
      return false
    if (photoUri.isNotBlank() && !photoUri.startsWith("content://")) return false
    val id = uid()
    classNotes.add(
      0,
      ClassroomNote(
        id,
        course.id,
        course.spaceId,
        course.sectionId,
        recordingId,
        seconds,
        text.trim(),
        photoUri,
        granted,
      ),
    )
    if (photoUri.isNotBlank())
      resources.add(
        0,
        CatalogResource(
          "photo_$id",
          course.spaceId,
          "Pizarra · ${course.title} · ${LearningRules.formatTimestamp(seconds)}",
          ResourceKind.IMAGE,
          course.title,
          "${granted.label} del salón",
          license = "Material de clase",
          fileUri = photoUri,
          mime = "image/*",
          origin = "Grabación · Sesión 10",
          courseId = course.id,
        ),
      )
    return true
  }

  fun freeze() =
    LearningState(
      plan = plan,
      purchasedIds = purchasedIds.toList(),
      enrolledIds = enrolledIds.toList(),
      cartIds = cartIds.toList(),
      resources = resources.toList(),
      shortcuts = shortcuts.toList(),
      posts = posts.toList(),
      replies = replies.toList(),
      hidden = hidden.toList(),
      reports = reports.toList(),
      classEvents = classEvents.toList(),
      classNotes = classNotes.toList(),
      votes = votes.toMap(),
      roles = roles.toMap(),
    )
}
