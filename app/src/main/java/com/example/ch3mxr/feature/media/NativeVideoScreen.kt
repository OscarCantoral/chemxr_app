package com.example.ch3mxr.feature.media

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.*
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.*
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import java.io.File

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun NativeVideoScreen(source: String, modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val owner = LocalLifecycleOwner.current
  var error by remember(source) { mutableStateOf(false) }
  var position by rememberSaveable(source) { mutableLongStateOf(0L) }
  val uri =
    remember(source) {
      if (source.startsWith("private:///")) {
        val file = File(context.filesDir, source.removePrefix("private:///"))
        require(file.canonicalPath.startsWith(context.filesDir.canonicalPath + File.separator))
        Uri.fromFile(file)
      } else Uri.parse(source)
    }
  val exo =
    remember(uri) {
      ExoPlayer.Builder(context.applicationContext).build().apply {
        setMediaItem(MediaItem.fromUri(uri))
        seekTo(position)
        prepare()
      }
    }
  DisposableEffect(owner, exo) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_PAUSE) {
        position = exo.currentPosition
        exo.pause()
      }
    }
    val listener =
      object : Player.Listener {
        override fun onPlayerError(exception: PlaybackException) {
          error = true
        }
      }
    exo.addListener(listener)
    owner.lifecycle.addObserver(observer)
    onDispose {
      owner.lifecycle.removeObserver(observer)
      exo.removeListener(listener)
      position = exo.currentPosition
      exo.release()
    }
  }
  Column(modifier.fillMaxSize()) {
    if (error) {
      Text("No se pudo reproducir este archivo.")
      TextButton(
        onClick = {
          error = false
          exo.prepare()
        }
      ) {
        Text("Reintentar")
      }
    }
    AndroidView(
      factory = { PlayerView(it).apply { player = exo } },
      modifier = Modifier.weight(1f).fillMaxWidth(),
    )
  }
}
