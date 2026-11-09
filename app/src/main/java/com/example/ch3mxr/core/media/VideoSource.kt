package com.example.ch3mxr.core.media

import java.net.URI

sealed interface VideoSource {
  data class Web(val embedUrl: String, val originalUrl: String) : VideoSource

  data class File(val uri: String) : VideoSource
}

object VideoSources {
  private val youtubeId = Regex("[A-Za-z0-9_-]{11}")

  fun parse(raw: String): VideoSource? =
    runCatching {
        val uri = URI(raw)
        val host = uri.host?.lowercase()
        if (uri.scheme in setOf("content", "private", "asset")) return VideoSource.File(raw)
        if (uri.scheme != "https" || uri.userInfo != null || uri.port !in setOf(-1, 443))
          return null
        if (
          host in
            setOf(
              "youtube.com",
              "www.youtube.com",
              "m.youtube.com",
              "youtu.be",
              "www.youtube-nocookie.com",
            )
        ) {
          val id =
            if (host == "youtu.be") uri.path.trim('/')
            else if (uri.path.startsWith("/embed/") || uri.path.startsWith("/shorts/"))
              uri.path.split('/').getOrNull(2)
            else uri.rawQuery?.split('&')?.firstOrNull { it.startsWith("v=") }?.substringAfter('=')
          if (id == null || !youtubeId.matches(id)) return null
          VideoSource.Web(
            "https://www.youtube.com/embed/$id?enablejsapi=1&playsinline=1&origin=https%3A%2F%2Fappassets.androidplatform.net",
            raw,
          )
        } else if (host in setOf("vimeo.com", "www.vimeo.com", "player.vimeo.com")) {
          val id = uri.path.trim('/').substringAfterLast('/')
          if (!id.matches(Regex("[0-9]{1,15}"))) return null
          VideoSource.Web("https://player.vimeo.com/video/$id", raw)
        } else if (uri.path.lowercase().endsWith(".mp4") || uri.path.lowercase().endsWith(".m3u8"))
          VideoSource.File(raw)
        else null
      }
      .getOrNull()

  fun mayNavigate(raw: String): Boolean =
    runCatching {
        val u = URI(raw)
        u.scheme == "https" &&
          u.userInfo == null &&
          u.port in setOf(-1, 443) &&
          u.host in setOf("www.youtube.com", "www.youtube-nocookie.com", "player.vimeo.com")
      }
      .getOrDefault(false)
}
