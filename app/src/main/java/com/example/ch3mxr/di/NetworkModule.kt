package com.example.ch3mxr.di

import com.example.ch3mxr.data.remote.ResourceService
import dagger.*
import dagger.hilt.*
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
  @Provides
  @Singleton
  fun client(): OkHttpClient =
    OkHttpClient.Builder()
      .connectTimeout(20, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .callTimeout(5, TimeUnit.MINUTES)
      .followRedirects(false)
      .followSslRedirects(false)
      .build()

  @Provides
  @Singleton
  fun retrofit(client: OkHttpClient): Retrofit =
    Retrofit.Builder().baseUrl("https://localhost.invalid/").client(client).build()

  @Provides
  fun resources(retrofit: Retrofit): ResourceService = retrofit.create(ResourceService::class.java)
}
