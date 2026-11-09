package com.example.ch3mxr.di

import android.content.Context
import androidx.room.Room
import com.example.ch3mxr.data.local.ChemXrDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
  @Provides
  @Singleton
  fun database(@ApplicationContext context: Context): ChemXrDatabase =
    Room.databaseBuilder(context, ChemXrDatabase::class.java, "chemxr.db").build()
}
