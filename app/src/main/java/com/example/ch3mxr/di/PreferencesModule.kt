package com.example.ch3mxr.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.*
import dagger.hilt.*
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {
  @Provides
  @Singleton
  fun preferences(@ApplicationContext context: Context): DataStore<Preferences> =
    PreferenceDataStoreFactory.create(
      migrations =
        listOf(
          SharedPreferencesMigration(context, "chemxr_demo_preferences"),
          SharedPreferencesMigration(context, "chemxr_demo_profile"),
        ),
      produceFile = { context.preferencesDataStoreFile("chemxr_preferences") },
    )
}
