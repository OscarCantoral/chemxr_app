package com.example.ch3mxr.di

import com.example.ch3mxr.data.offline.RoomOfflineRepository
import com.example.ch3mxr.feature.library.offline.domain.OfflineResourceRepository
import dagger.*
import dagger.hilt.*
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
  @Binds abstract fun offline(implementation: RoomOfflineRepository): OfflineResourceRepository
}
