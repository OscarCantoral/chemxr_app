package com.example.ch3mxr

import android.content.Context
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.test.core.app.ApplicationProvider
import com.example.ch3mxr.data.preferences.PreferencesRepository
import java.io.File
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class PreferencesMigrationTest {
  @Test
  fun migratesTypedLegacyValuesAndPersistsChanges() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    context
      .getSharedPreferences("test_preferences", 0)
      .edit()
      .clear()
      .putString("theme_mode", "LIGHT")
      .putBoolean("enable_notifications", false)
      .commit()
    val file = File(context.cacheDir, "preferences-${System.nanoTime()}.preferences_pb")
    val job = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.IO + job)
    try {
      val store =
        PreferenceDataStoreFactory.create(
          migrations = listOf(SharedPreferencesMigration(context, "test_preferences")),
          scope = scope,
          produceFile = { file },
        )
      val repo = PreferencesRepository(store)
      val initial = repo.values.first()
      assertEquals("LIGHT", initial["theme_mode"])
      assertEquals("false", initial["enable_notifications"])
      repo.string("theme_mode", "DARK")
      repo.boolean("enable_notifications", true)
      assertEquals("DARK", repo.values.first()["theme_mode"])
      assertEquals("true", repo.values.first()["enable_notifications"])
    } finally {
      job.cancelAndJoin()
      file.delete()
    }
  }
}
