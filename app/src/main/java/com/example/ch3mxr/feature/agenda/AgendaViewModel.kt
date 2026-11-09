package com.example.ch3mxr.feature.agenda

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
class AgendaViewModel @Inject constructor(private val repo: PersonalRepository) : ViewModel() {
  private val reload = MutableStateFlow(0)
  val state =
    reload
      .flatMapLatest {
        repo.agenda
          .map<List<AgendaItem>, LoadState<List<AgendaItem>>> { LoadState.Ready(it) }
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

  fun add(item: AgendaItem, onSaved: () -> Unit) {
    launch {
      repo.addEvent(item)
      onSaved()
    }
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
