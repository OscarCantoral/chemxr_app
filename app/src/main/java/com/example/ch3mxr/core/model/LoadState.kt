package com.example.ch3mxr.core.model
sealed interface LoadState<out T> {
  data object Loading : LoadState<Nothing>

  data class Ready<T>(val value: T) : LoadState<T>

  data class Failed(val message: String) : LoadState<Nothing>
}
