package com.example.ch3mxr.data.pdf

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.*
import javax.inject.Inject

class PdfRepository @Inject constructor(@param:ApplicationContext private val context: Context) {
  fun open(source: String): PdfDocument {
    var temporary: File? = null
    val descriptor =
      when {
        source.startsWith("asset:///") -> {
          val path = source.removePrefix("asset:///")
          require(".." !in path.split('/'))
          temporary = File.createTempFile("manual-", ".pdf", context.cacheDir)
          try {
            context.assets.open(path).use { input ->
              temporary.outputStream().use { input.copyTo(it) }
            }
            ParcelFileDescriptor.open(temporary, ParcelFileDescriptor.MODE_READ_ONLY)
          } catch (e: Exception) {
            temporary.delete()
            throw e
          }
        }
        source.startsWith("content://") ->
          context.contentResolver.openFileDescriptor(Uri.parse(source), "r")
            ?: throw IOException("Document unavailable")
        source.startsWith("private:///") -> {
          val file = File(context.filesDir, source.removePrefix("private:///"))
          require(file.canonicalPath.startsWith(context.filesDir.canonicalPath + File.separator))
          ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        }
        else -> throw IOException("Open a local PDF or download it first")
      }
    return try {
      PdfDocument(PdfRenderer(descriptor), descriptor, temporary)
    } catch (e: Exception) {
      descriptor.close()
      temporary?.delete()
      throw e
    }
  }
}

class PdfDocument(
  private val renderer: PdfRenderer,
  private val descriptor: ParcelFileDescriptor,
  private val temporary: File?,
) : Closeable {
  private var closed = false
  val pages: Int = renderer.pageCount

  @Synchronized
  fun render(index: Int, width: Int = 1400): Bitmap {
    check(!closed)
    require(index in 0 until pages)
    return renderer.openPage(index).use { page ->
      val height = (width.toDouble() * page.height / page.width).toInt().coerceIn(1, 3000)
      Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also { bitmap ->
        bitmap.eraseColor(Color.WHITE)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
      }
    }
  }

  @Synchronized
  override fun close() {
    if (!closed) {
      closed = true
      try {
        renderer.close()
      } finally {
        descriptor.close()
        temporary?.delete()
      }
    }
  }
}
