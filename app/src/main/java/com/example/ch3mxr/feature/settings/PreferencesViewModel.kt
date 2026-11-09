package com.example.ch3mxr.feature.settings

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.*
import com.example.ch3mxr.core.model.LoadState
import com.example.ch3mxr.data.preferences.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class PreferencesViewModel @Inject constructor(private val repo: PreferencesRepository) :
  ViewModel() {
  private val reload = MutableStateFlow(0)
  val state =
    reload
      .flatMapLatest {
        repo.values
          .map<Map<String, String>, LoadState<Map<String, String>>> { LoadState.Ready(it) }
          .catch {
            emit(
              LoadState.Failed("No se pudieron cargar tus preferencias. Tus datos se conservan.")
            )
          }
      }
      .stateIn(viewModelScope, SharingStarted.Eagerly, LoadState.Loading)
  private val _errors = MutableSharedFlow<String>(extraBufferCapacity = 4)
  val errors = _errors.asSharedFlow()

  fun retry() {
    reload.value++
  }

  fun setString(key: String, value: String) {
    save { repo.string(key, value) }
  }

  fun setBoolean(key: String, value: Boolean) {
    save { repo.boolean(key, value) }
  }

  fun profile(values: Map<String, String>, onResult: (Boolean) -> Unit = {}) {
    viewModelScope.launch {
      try {
        repo.profile(values)
        onResult(true)
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        _errors.emit("No se pudo guardar el perfil.")
        onResult(false)
      }
    }
  }

  private fun save(block: suspend () -> Unit) {
    viewModelScope.launch {
      try {
        block()
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        _errors.emit("No se pudo guardar el cambio. Inténtalo nuevamente.")
      }
    }
  }
}

/** UI reads a snapshot; writes are delegated to the lifecycle-owned ViewModel. */
class PreferencesUi(val values: Map<String, String>, private val actions: PreferencesViewModel) {
  fun getString(key: String, default: String): String = values[key] ?: default

  fun getBoolean(key: String, default: Boolean): Boolean =
    values[key]?.toBooleanStrictOrNull() ?: default

  fun setString(key: String, value: String) = actions.setString(key, value)

  fun setBoolean(key: String, value: Boolean) = actions.setBoolean(key, value)

  fun profile(values: Map<String, String>, onResult: (Boolean) -> Unit = {}) =
    actions.profile(values, onResult)
}

val LocalPreferences =
  staticCompositionLocalOf<PreferencesUi> { error("Preferences are provided by AppRoot") }
