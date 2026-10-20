package com.example.ch3mxr.feature.library.offline.domain

/** Metadatos; el archivo en si NO se guardara como BLOB en Room. */
enum class ResourceType { PDF, VIDEO, AUDIO, DOCUMENT, MODEL_3D, AR_ASSET, OTHER }

enum class DownloadState { NOT_DOWNLOADED, QUEUED, DOWNLOADING, AVAILABLE, FAILED, NEEDS_UPDATE }

data class OfflineResource(
    val resourceId: String,
    val courseId: String,
    val title: String,
    val type: ResourceType,
    val state: DownloadState = DownloadState.NOT_DOWNLOADED,
    val localRelativePath: String? = null,
    val version: String? = null,
    val sha256: String? = null
)

/** Contrato futuro: implementacion con Room + archivos privados + descargas. */
interface OfflineResourceRepository {
    suspend fun listByCourse(courseId: String): List<OfflineResource>
    suspend fun getById(resourceId: String): OfflineResource?
    suspend fun requestDownload(resourceId: String)
    suspend fun deleteLocalCopy(resourceId: String)
}
