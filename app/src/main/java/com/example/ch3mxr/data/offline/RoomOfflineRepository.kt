package com.example.ch3mxr.data.offline

import android.content.Context
import com.example.ch3mxr.core.model.*
import com.example.ch3mxr.data.learning.LearningRepository
import com.example.ch3mxr.data.local.*
import com.example.ch3mxr.data.remote.ResourceService
import com.example.ch3mxr.feature.library.offline.domain.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.*
import java.net.URI
import java.nio.file.*
import java.security.MessageDigest
import javax.inject.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
class RoomOfflineRepository
@Inject
constructor(
  @param:ApplicationContext private val context: Context,
  private val db: ChemXrDatabase,
  private val learning: LearningRepository,
  private val service: ResourceService,
) : OfflineResourceRepository {
  private val locks = java.util.concurrent.ConcurrentHashMap<String, Mutex>()
  private val recoveryLock = Mutex()
  private val dao
    get() = db.offline()

  private var recovered = false

  private suspend fun recoverLocked() =
    recoveryLock.withLock {
      if (!recovered) {
        dao.recoverInterrupted()
        File(context.filesDir, "resources")
          .listFiles()
          ?.filter { it.name.startsWith("download-") && it.name.endsWith(".part") }
          ?.forEach { it.delete() }
        recovered = true
      }
    }

  fun observe(id: String) =
    flow {
        recoverLocked()
        emitAll(dao.observe(id).map { it?.model() })
      }
      .flowOn(Dispatchers.IO)

  override suspend fun listByCourse(courseId: String) =
    withContext(Dispatchers.IO) { dao.byCourse(courseId).map { it.model() } }

  override suspend fun getById(resourceId: String) =
    withContext(Dispatchers.IO) { dao.find(resourceId)?.model() }

  override suspend fun requestDownload(resourceId: String) =
    withContext(Dispatchers.IO) {
      locks
        .getOrPut(resourceId) { Mutex() }
        .withLock {
          recoverLocked()
          val item =
            learning.state.first().resources.firstOrNull { it.id == resourceId }
              ?: error("Resource no longer exists")
          require(
            item.kind in
              setOf(ResourceKind.PDF, ResourceKind.VIDEO, ResourceKind.AUDIO, ResourceKind.MODEL_3D)
          )
          val previous = dao.find(resourceId)
          if (
            previous?.state == DownloadState.AVAILABLE.name &&
              previous.localRelativePath?.let { File(context.filesDir, it).isFile } == true
          )
            return@withLock
          val type =
            when (item.kind) {
              ResourceKind.PDF -> ResourceType.PDF
              ResourceKind.VIDEO -> ResourceType.VIDEO
              ResourceKind.AUDIO -> ResourceType.AUDIO
              else -> ResourceType.MODEL_3D
            }
          val base =
            OfflineEntity(
              item.id,
              item.courseId,
              item.title,
              type.name,
              DownloadState.DOWNLOADING.name,
              null,
              null,
              null,
            )
          dao.save(base)
          val directory = File(context.filesDir, "resources").apply { mkdirs() }
          val name =
            MessageDigest.getInstance("SHA-256").digest(item.id.toByteArray()).joinToString("") {
              "%02x".format(it)
            }
          val temp = File.createTempFile("download-", ".part", directory)
          try {
            val hash = MessageDigest.getInstance("SHA-256")
            fun copy(input: InputStream) {
              input.use { source ->
                FileOutputStream(temp).use { target ->
                  val buffer = ByteArray(65536)
                  var count = 0L
                  while (true) {
                    val size = source.read(buffer)
                    if (size < 0) break
                    ensureActive()
                    count += size
                    require(count <= 200L * 1024 * 1024) { "Resource exceeds 200 MiB" }
                    hash.update(buffer, 0, size)
                    target.write(buffer, 0, size)
                  }
                  target.fd.sync()
                }
              }
            }
            if (item.fileUri.startsWith("asset:///")) {
              val path = item.fileUri.removePrefix("asset:///")
              require(".." !in path.split('/'))
              copy(context.assets.open(path))
            } else {
              val uri = URI(item.url)
              require(
                uri.scheme == "https" &&
                  uri.host != null &&
                  uri.userInfo == null &&
                  uri.port in setOf(-1, 443)
              )
              require(
                !uri.path.lowercase().endsWith(".m3u8") && !uri.path.lowercase().endsWith(".mpd")
              ) {
                "Streaming playlists require segment downloads"
              }
              val response = service.download(item.url)
              if (!response.isSuccessful) {
                response.errorBody()?.close()
                throw IOException("HTTP ${response.code()}")
              }
              val body = response.body() ?: throw IOException("Empty response")
              body.use {
                require(it.contentLength() <= 200L * 1024 * 1024)
                require(it.contentType()?.toString()?.contains("text/html") != true) {
                  "The URL is a page, not a direct resource"
                }
                val contentType = it.contentType()?.toString().orEmpty().lowercase()
                require("mpegurl" !in contentType && "dash+xml" !in contentType) {
                  "Streaming playlist is not an offline file"
                }
                copy(it.byteStream())
              }
            }
            require(temp.length() > 0)
            if (type == ResourceType.VIDEO || type == ResourceType.AUDIO) {
              val header =
                temp.inputStream().bufferedReader().use { reader ->
                  val chars = CharArray(32)
                  val n = reader.read(chars)
                  if (n > 0) String(chars, 0, n) else ""
                }
              require(!header.trimStart().startsWith("#EXTM3U") && "<MPD" !in header) {
                "Streaming playlist is not an offline file"
              }
            }
            if (type == ResourceType.PDF)
              temp.inputStream().use {
                require(
                  String(
                    ByteArray(5).also { bytes -> java.io.DataInputStream(it).readFully(bytes) }
                  ) == "%PDF-"
                )
              }
            if (type == ResourceType.MODEL_3D) validateGlb(temp)
            val target = File(directory, name)
            try {
              Files.move(
                temp.toPath(),
                target.toPath(),
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE,
              )
            } catch (e: AtomicMoveNotSupportedException) {
              Files.move(temp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
            dao.save(
              base.copy(
                state = DownloadState.AVAILABLE.name,
                localRelativePath = "resources/$name",
                sha256 = hash.digest().joinToString("") { "%02x".format(it) },
              )
            )
          } catch (e: Exception) {
            withContext(NonCancellable) { dao.save(base.copy(state = DownloadState.FAILED.name)) }
            throw e
          } finally {
            temp.delete()
          }
        }
    }

  override suspend fun deleteLocalCopy(resourceId: String) =
    withContext(Dispatchers.IO) {
      locks
        .getOrPut(resourceId) { Mutex() }
        .withLock {
          val old = dao.find(resourceId) ?: return@withLock
          old.localRelativePath?.let { path ->
            val file = File(context.filesDir, path)
            require(file.canonicalPath.startsWith(context.filesDir.canonicalPath + File.separator))
            if (file.exists() && !file.delete()) throw IOException("Cannot delete local resource")
          }
          dao.save(
            old.copy(
              state = DownloadState.NOT_DOWNLOADED.name,
              localRelativePath = null,
              sha256 = null,
            )
          )
        }
    }

  fun localUri(path: String): String {
    require(!path.startsWith('/') && ".." !in path.split('/'))
    return "private:///$path"
  }

  private fun OfflineEntity.model() =
    OfflineResource(
      resourceId,
      courseId,
      title,
      ResourceType.valueOf(type),
      if (
        state == DownloadState.AVAILABLE.name &&
          localRelativePath?.let { File(context.filesDir, it).isFile } != true
      )
        DownloadState.FAILED
      else DownloadState.valueOf(state),
      localRelativePath,
      version,
      sha256,
    )
}

fun validateGlb(file: File) {
  file.inputStream().use { input ->
    val header = ByteArray(12)
    java.io.DataInputStream(input).readFully(header)
    val buffer = java.nio.ByteBuffer.wrap(header).order(java.nio.ByteOrder.LITTLE_ENDIAN)
    require(buffer.int == 0x46546c67 && buffer.int == 2 && buffer.int.toLong() == file.length()) {
      "Invalid GLB header"
    }
  }
}
