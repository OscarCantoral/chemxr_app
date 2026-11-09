package com.example.ch3mxr.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import javax.inject.*
import kotlinx.coroutines.flow.map

@Singleton
class PreferencesRepository @Inject constructor(private val store: DataStore<Preferences>) {
  val values =
    store.data.map { p -> p.asMap().mapKeys { it.key.name }.mapValues { it.value.toString() } }

  suspend fun string(key: String, value: String) {
    require(known(key))
    store.edit { it[stringPreferencesKey(key)] = value }
  }

  suspend fun boolean(key: String, value: Boolean) {
    require(known(key))
    store.edit { it[booleanPreferencesKey(key)] = value }
  }

  suspend fun profile(values: Map<String, String>) {
    require(values.keys.all { it in profileKeys })
    store.edit { p -> values.forEach { (k, v) -> p[stringPreferencesKey(k)] = v } }
  }

  private fun known(key: String) =
    key in profileKeys ||
      key in
        setOf(
          "theme_mode",
          "personal_accent",
          "selected_space_id",
          "enable_notifications",
          "push_academic",
          "email_digest",
          "ai_reminders",
          "ad_notifications",
        ) ||
      key in
        listOf("personal", "utp", "senati").flatMap {
          listOf("period_$it", "calendar_$it", "notifications_$it")
        }

  companion object {
    val profileKeys = setOf("bio", "interests", "country", "department", "province", "district")
  }
}
