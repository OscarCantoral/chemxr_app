package com.example.ch3mxr.data.learning

import android.content.Context
import com.example.ch3mxr.core.model.*
import com.example.ch3mxr.data.demo.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import org.json.*

/** Import only; never clears the source preferences. Invalid input aborts the transaction. */
class LegacyLearningReader
@Inject
constructor(@param:ApplicationContext private val context: Context) {
  fun read(): LearningState {
    val raw =
      context
        .getSharedPreferences("chemxr_learning_v1", Context.MODE_PRIVATE)
        .getString("state", null)
    val x = if (raw == null) JSONObject() else JSONObject(raw)
    require(x.optInt("schema", 1) == 1) { "Unsupported legacy schema" }
    return LearningState(
      plan = enumValue(x.s("plan"), StudentPlan.FREE),
      purchasedIds = x.strings("purchased").filter { it in DemoCourses.purchased },
      enrolledIds = if (x.has("enrolled")) x.strings("enrolled") else DemoCourses.initialEnrollment,
      cartIds = if (x.has("cart")) x.strings("cart") else DemoCourses.initialSelection,
      resources =
        (if (x.has("resources")) x.objects("resources").map { it.resource() } else seedResources())
          .let { old -> old + bundledResources().filter { b -> old.none { it.id == b.id } } },
      shortcuts =
        x.objects("shortcuts").map {
          SavedShortcut(it.s("id"), it.s("space"), it.s("title"), it.s("kind"), it.s("route"))
        },
      posts =
        if (x.has("posts"))
          x.objects("posts").map {
            CommunityPost(
              it.s("id"),
              it.s("space"),
              it.s("author"),
              it.s("initials"),
              it.s("text"),
              it.optInt("score"),
              it.s("resource"),
              it.s("group"),
            )
          }
        else seedPosts(),
      replies =
        if (x.has("replies"))
          x.objects("replies").map {
            CommunityReply(
              it.s("id"),
              it.s("post"),
              it.s("author"),
              it.s("initials"),
              it.s("text"),
              it.optInt("score"),
              it.s("resource"),
            )
          }
        else seedReplies(),
      votes = x.intMap("votes"),
      roles =
        x.intMap("roles").mapValues { (_, ordinal) ->
          AcademicRole.entries.getOrElse(ordinal) { AcademicRole.STUDENT }
        },
      hidden = x.strings("hidden"),
      reports = x.strings("reports"),
      classEvents =
        x.objects("events").map {
          ClassroomEvent(
            it.s("id"),
            it.s("course"),
            it.s("space"),
            it.s("section"),
            it.s("title"),
            it.s("type"),
            it.s("start"),
            it.s("end"),
            enumValue(it.s("role"), AcademicRole.STUDENT),
          )
        },
      classNotes =
        x.objects("classNotes").map {
          ClassroomNote(
            it.s("id"),
            it.s("course"),
            it.s("space"),
            it.s("section"),
            it.s("recording"),
            it.optInt("seconds"),
            it.s("text"),
            it.s("photo"),
            enumValue(it.s("role"), AcademicRole.STUDENT),
          )
        },
    )
  }
}

private fun JSONObject.s(key: String) = optString(key, "")

private fun JSONObject.array(key: String) = if (has(key)) getJSONArray(key) else JSONArray()

private fun JSONObject.objects(key: String) =
  array(key).let { a -> (0 until a.length()).map { a.getJSONObject(it) } }

private fun JSONObject.strings(key: String) =
  array(key).let { a -> (0 until a.length()).map { a.getString(it) } }

private fun JSONObject.intMap(key: String): Map<String, Int> {
  val obj = if (has(key)) getJSONObject(key) else return emptyMap()
  return obj.keys().asSequence().associateWith { obj.getInt(it) }
}

private inline fun <reified T : Enum<T>> enumValue(s: String, default: T) =
  if (s.isBlank()) default else enumValueOf<T>(s)

private fun JSONObject.resource() =
  CatalogResource(
    s("id"),
    s("space"),
    s("title"),
    enumValue(s("kind"), ResourceKind.OTHER),
    s("category"),
    s("author"),
    s("tags"),
    s("license"),
    s("url"),
    s("file"),
    s("mime"),
    s("origin"),
    s("course"),
  )
