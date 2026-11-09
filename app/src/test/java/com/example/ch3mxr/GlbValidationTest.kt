package com.example.ch3mxr

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ch3mxr.data.offline.validateGlb
import java.io.File
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class GlbValidationTest {
  @Test
  fun bundledModelHasConsistentHeader() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val file = File.createTempFile("model-", ".glb", context.cacheDir)
    try {
      context.assets.open("models/atom.glb").use { input ->
        file.outputStream().use { input.copyTo(it) }
      }
      validateGlb(file)
    } finally {
      file.delete()
    }
  }

  @Test
  fun invalidContainerLengthIsRejectedBeforeRendering() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val bytes = context.assets.open("models/atom.glb").use { it.readBytes() }
    bytes[8] = 0
    val file = File.createTempFile("model-", ".glb", context.cacheDir)
    try {
      file.writeBytes(bytes)
      try {
        validateGlb(file)
        fail("Invalid GLB must fail")
      } catch (expected: IllegalArgumentException) {}
    } finally {
      file.delete()
    }
  }
}
