package com.example.ch3mxr.feature.media

import androidx.lifecycle.*
import com.example.ch3mxr.core.model.CatalogResource
import com.example.ch3mxr.data.learning.LearningRepository
import com.example.ch3mxr.data.offline.RoomOfflineRepository
import com.example.ch3mxr.feature.library.offline.domain.OfflineResource
import dagger.assisted.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class ResourceUiState(
  val item: CatalogResource? = null,
  val offline: OfflineResource? = null,
  val loading: Boolean = true,
  val error: String? = null,
)

/** Runtime resource and space IDs are the reason for an assisted factory. */
@HiltViewModel(assistedFactory = ResourceViewModel.Factory::class)
class ResourceViewModel
@AssistedInject
constructor(
  private val learning: LearningRepository,
  private val models: com.example.ch3mxr.data.media.ModelSourceResolver,
  private val offline: RoomOfflineRepository,
  @Assisted("resourceId") private val resourceId: String,
  @Assisted("spaceId") private val spaceId: String,
) : ViewModel() {
  @AssistedFactory
  interface Factory {
    fun create(
      @Assisted("resourceId") resourceId: String,
      @Assisted("spaceId") spaceId: String,
    ): ResourceViewModel
  }

  private val error = MutableStateFlow<String?>(null)
  val state =
    combine(learning.state, offline.observe(resourceId), error) { l, o, e ->
        ResourceUiState(
          l.resources.firstOrNull { it.id == resourceId && it.spaceId == spaceId },
          o,
          false,
          e,
        )
      }
      .catch { emit(ResourceUiState(loading = false, error = "No se pudo cargar el recurso.")) }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ResourceUiState())
  private var transfer: Job? = null

  fun download() {
    if (transfer?.isActive == true) return
    transfer =
      viewModelScope.launch {
        error.value = null
        try {
          require(
            learning.state.first().resources.any { it.id == resourceId && it.spaceId == spaceId }
          )
          offline.requestDownload(resourceId)
        } catch (e: CancellationException) {
          throw e
        } catch (e: Exception) {
          error.value =
            "No se pudo descargar. Usa un enlace directo al archivo y comprueba tu conexión."
        }
      }
  }

  fun cancel() {
    transfer?.cancel()
  }

  fun delete() {
    viewModelScope.launch {
      try {
        offline.deleteLocalCopy(resourceId)
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        error.value = "No se pudo eliminar la copia local."
      }
    }
  }

  suspend fun modelLocation(source: String) = models.resolve(source)

  fun source(state: ResourceUiState): String? =
    state.offline
      ?.takeIf {
        it.state == com.example.ch3mxr.feature.library.offline.domain.DownloadState.AVAILABLE
      }
      ?.localRelativePath
      ?.let(offline::localUri) ?: state.item?.let { it.fileUri.ifBlank { it.url } }
}
