package com.example.ch3mxr.core.model

import java.net.URI
import java.util.UUID

fun uid(): String = UUID.randomUUID().toString()

fun isWebUrl(value: String): Boolean =
  runCatching {
      URI(value.trim()).let {
        it.scheme?.lowercase() in listOf("https", "http") &&
          !it.host.isNullOrBlank() &&
          it.userInfo == null
      }
    }
    .getOrDefault(false)
