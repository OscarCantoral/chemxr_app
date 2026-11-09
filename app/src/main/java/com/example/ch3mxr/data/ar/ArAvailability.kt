package com.example.ch3mxr.data.ar

import android.content.Context
import com.google.ar.core.ArCoreApk
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class ArAvailability @Inject constructor(@param:ApplicationContext private val context: Context) {
  suspend fun check(): ArCoreApk.Availability = suspendCancellableCoroutine { c ->
    ArCoreApk.getInstance().checkAvailabilityAsync(context) { if (c.isActive) c.resume(it) }
  }
}
