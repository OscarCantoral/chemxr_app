package com.example.ch3mxr.feature.ar

import androidx.lifecycle.*
import com.example.ch3mxr.data.ar.ArAvailability
import com.google.ar.core.ArCoreApk
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@HiltViewModel
class ArViewModel @Inject constructor(private val checker: ArAvailability) : ViewModel() {
  private val _availability = MutableStateFlow<ArCoreApk.Availability?>(null)
  val availability = _availability.asStateFlow()

  fun refresh() {
    viewModelScope.launch {
      try {
        _availability.value = checker.check()
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        _availability.value = ArCoreApk.Availability.UNKNOWN_ERROR
      }
    }
  }

  init {
    refresh()
  }
}
