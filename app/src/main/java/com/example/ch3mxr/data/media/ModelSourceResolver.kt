package com.example.ch3mxr.data.media

import android.content.Context
import android.net.Uri
import com.example.ch3mxr.data.offline.validateGlb
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.*
import java.security.MessageDigest
import javax.inject.Inject
import kotlinx.coroutines.*

class ModelSourceResolver
@Inject
constructor(@param:ApplicationContext private val context: Context) {
  suspend fun resolve(source: String): String =
    withContext(Dispatchers.IO) {
      when {
        source.startsWith("asset:///") ->
          source.removePrefix("asset:///").also { require(".." !in it.split('/')) }
        source.startsWith("private:///") ->
          File(context.filesDir, source.removePrefix("private:///"))
            .also {
              require(it.canonicalPath.startsWith(context.filesDir.canonicalPath + File.separator))
              validateGlb(it)
            }
            .path
        source.startsWith("content://") -> {
          val directory = File(context.cacheDir, "models").apply { mkdirs() }
          val temporary = File.createTempFile("selected-", ".glb", directory)
          try {
            (context.contentResolver.openInputStream(Uri.parse(source))
                ?: throw IOException("Model unavailable"))
              .use { input ->
                temporary.outputStream().use { output ->
                  val buffer = ByteArray(65536)
                  var total = 0L
                  while (true) {
                    ensureActive()
                    val n = input.read(buffer)
                    if (n < 0) break
                    total += n
                    require(total <= 200L * 1024 * 1024)
                    output.write(buffer, 0, n)
                  }
                }
              }
            validateGlb(temporary)
            val name =
              MessageDigest.getInstance("SHA-256").digest(source.toByteArray()).joinToString("") {
                "%02x".format(it)
              } + ".glb"
            val target = File(directory, name)
            java.nio.file.Files.move(
              temporary.toPath(),
              target.toPath(),
              java.nio.file.StandardCopyOption.REPLACE_EXISTING,
            )
            target.path
          } finally {
            temporary.delete()
          }
        }
        else -> throw IOException("Download the model before opening it")
      }
    }
}
