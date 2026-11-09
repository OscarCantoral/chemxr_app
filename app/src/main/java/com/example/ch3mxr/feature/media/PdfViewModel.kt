package com.example.ch3mxr.feature.media

import android.graphics.Bitmap
import androidx.lifecycle.*
import com.example.ch3mxr.data.pdf.*
import dagger.assisted.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class PdfUiState(
  val page: Int = 0,
  val pages: Int = 0,
  val bitmap: Bitmap? = null,
  val loading: Boolean = true,
  val error: String? = null,
)

@HiltViewModel(assistedFactory = PdfViewModel.Factory::class)
class PdfViewModel
@AssistedInject
constructor(private val repo: PdfRepository, @Assisted private val source: String) : ViewModel() {
  @AssistedFactory
  interface Factory {
    fun create(source: String): PdfViewModel
  }

  private val _state = MutableStateFlow(PdfUiState())
  val state = _state.asStateFlow()
  private val lock = Any()
  private var document: PdfDocument? = null
  private var job: Job? = null

  fun open() {
    if (document != null || job?.isActive == true) return
    job =
      viewModelScope.launch(Dispatchers.IO) {
        _state.update { it.copy(bitmap = null, loading = true, error = null) }
        try {
          val opened = repo.open(source)
          synchronized(lock) {
            if (!isActive) {
              opened.close()
              return@launch
            }
            document = opened
          }
          val resumePage = _state.value.page.coerceIn(0, opened.pages - 1)
          val bitmap = opened.render(resumePage)
          ensureActive()
          _state.value = PdfUiState(resumePage, opened.pages, bitmap, false)
        } catch (e: CancellationException) {
          throw e
        } catch (e: Exception) {
          _state.value =
            PdfUiState(
              loading = false,
              error = "No se pudo abrir el PDF. Revisa el archivo y su permiso de lectura.",
            )
        }
      }
  }

  fun page(index: Int) {
    val doc = document ?: return
    if (index !in 0 until doc.pages) return
    job?.cancel()
    job =
      viewModelScope.launch(Dispatchers.IO) {
        try {
          _state.update { it.copy(loading = true, error = null) }
          val bitmap = doc.render(index)
          ensureActive()
          _state.value = PdfUiState(index, doc.pages, bitmap, false)
        } catch (e: CancellationException) {
          throw e
        } catch (e: Exception) {
          _state.update { it.copy(loading = false, error = "No se pudo cargar esta página.") }
        }
      }
  }

  fun release() {
    job?.cancel()
    val old = synchronized(lock) { document.also { document = null } }
    _state.update { it.copy(bitmap = null, loading = true, error = null) }
    old?.let { java.util.concurrent.ForkJoinPool.commonPool().execute { it.close() } }
  }

  override fun onCleared() {
    release()
  }
}
