package com.example.ch3mxr.feature.notes

import androidx.lifecycle.*
import com.example.ch3mxr.core.model.*
import com.example.ch3mxr.data.local.*
import com.example.ch3mxr.data.personal.PersonalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class NotesViewModel @Inject constructor(private val repo: PersonalRepository) : ViewModel() {
  private val reload = MutableStateFlow(0)
  val state =
    reload
      .flatMapLatest {
        repo.notes
          .map<List<UserNote>, LoadState<List<UserNote>>> { LoadState.Ready(it) }
          .catch {
            emit(
              LoadState.Failed("No se pudieron recuperar tus datos. La copia anterior se conserva.")
            )
          }
      }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LoadState.Loading)
  private val _error = MutableStateFlow<String?>(null)
  val error = _error.asStateFlow()

  fun retry() {
    reload.value++
  }

  fun add(text: String, photo: String, onSaved: () -> Unit) {
    launch {
      repo.addNote(text, photo)
      onSaved()
    }
  }

  fun delete(id: String) {
    launch { repo.deleteNote(id) }
  }

  private fun launch(block: suspend () -> Unit) {
    viewModelScope.launch {
      try {
        _error.value = null
        block()
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        _error.value = "No se pudo guardar. Inténtalo nuevamente."
      }
    }
  }
}
