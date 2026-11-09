package com.example.ch3mxr.feature.media

import android.app.Activity
import android.content.*
import android.graphics.Color
import android.net.Uri
import android.view.*
import android.webkit.*
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.*
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ch3mxr.core.media.*

@Composable
fun WebVideoScreen(source: VideoSource.Web, modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val owner = LocalLifecycleOwner.current
  var player by remember { mutableStateOf<SecureVideoView?>(null) }
  var error by remember { mutableStateOf(false) }
  var fullscreen by remember { mutableStateOf(false) }
  BackHandler(enabled = fullscreen) { player?.exitFullscreen() }
  val currentPlayer = player
  DisposableEffect(owner, currentPlayer) {
    val observer = LifecycleEventObserver { _, event ->
      when (event) {
        Lifecycle.Event.ON_RESUME -> currentPlayer?.resume()
        Lifecycle.Event.ON_PAUSE -> currentPlayer?.pause()
        else -> Unit
      }
    }
    owner.lifecycle.addObserver(observer)
    if (owner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) currentPlayer?.resume()
    onDispose {
      owner.lifecycle.removeObserver(observer)
      currentPlayer?.destroy()
    }
  }
  Column(modifier.fillMaxSize()) {
    if (error)
      Text(
        "El proveedor no pudo reproducir el video aquí. Puedes abrirlo en su sitio.",
        Modifier.padding(12.dp),
      )
    Row {
      TextButton(
        onClick = {
          error = false
          player?.load(source)
        }
      ) {
        Text("Recargar")
      }
      TextButton(
        onClick = {
          runCatching {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(source.originalUrl)))
          }
        }
      ) {
        Text("Abrir en navegador")
      }
    }
    AndroidView(
      factory = { ctx ->
        SecureVideoView(ctx, onError = { error = true }, onFullscreen = { fullscreen = it }).also {
          player = it
          it.load(source)
        }
      },
      modifier = Modifier.weight(1f).fillMaxWidth(),
    )
  }
}

private tailrec fun Context.activity(): Activity? =
  when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.activity()
    else -> null
  }

class SecureVideoView(
  context: Context,
  private val onError: () -> Unit,
  private val onFullscreen: (Boolean) -> Unit,
) : FrameLayout(context) {
  private val web = WebView(context)
  private var custom: View? = null
  private var callback: WebChromeClient.CustomViewCallback? = null
  private var previousOrientation: Int? = null
  private var destroyed = false

  init {
    setBackgroundColor(Color.BLACK)
    addView(web, LayoutParams(-1, -1))
    web.settings.apply {
      javaScriptEnabled = true
      domStorageEnabled = true
      allowFileAccess = false
      allowContentAccess = false
      mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
      mediaPlaybackRequiresUserGesture = true
      setGeolocationEnabled(false)
      javaScriptCanOpenWindowsAutomatically = false
      setSupportMultipleWindows(false)
      if (android.os.Build.VERSION.SDK_INT >= 26) safeBrowsingEnabled = true
    }
    web.webViewClient =
      object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
          val url = request.url.toString()
          if (VideoSources.mayNavigate(url)) return false
          if (request.isForMainFrame && request.url.scheme == "https")
            runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, request.url)) }
          return true
        }

        override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
          onError()
          view.post { destroy() }
          return true
        }

        override fun onReceivedError(
          view: WebView,
          request: WebResourceRequest,
          error: WebResourceError,
        ) {
          if (request.isForMainFrame) onError()
        }

        override fun onReceivedHttpError(
          view: WebView,
          request: WebResourceRequest,
          response: WebResourceResponse,
        ) {
          if (request.isForMainFrame) onError()
        }
      }
    web.webChromeClient =
      object : WebChromeClient() {
        override fun onShowCustomView(view: View, cb: CustomViewCallback) {
          if (custom != null) {
            cb.onCustomViewHidden()
            return
          }
          previousOrientation = context.activity()?.requestedOrientation
          custom = view
          callback = cb
          web.visibility = View.GONE
          addView(view, LayoutParams(-1, -1))
          onFullscreen(true)
        }

        override fun onHideCustomView() {
          exitFullscreen()
        }

        override fun onPermissionRequest(request: PermissionRequest) {
          request.deny()
        }
      }
  }

  fun load(source: VideoSource.Web) {
    if (destroyed) return
    require(VideoSources.mayNavigate(source.embedUrl))
    val html =
      """<!doctype html><html><head><meta name="viewport" content="width=device-width, initial-scale=1"></head><body style="margin:0;background:#000"><iframe src="${source.embedUrl.replace("&","&amp;")}" style="border:0;width:100%;height:100vh" allow="encrypted-media; fullscreen; picture-in-picture" allowfullscreen referrerpolicy="strict-origin-when-cross-origin"></iframe></body></html>"""
    web.loadDataWithBaseURL(
      "https://appassets.androidplatform.net/",
      html,
      "text/html",
      "UTF-8",
      null,
    )
  }

  fun exitFullscreen() {
    custom?.let { removeView(it) }
    custom = null
    val previousCallback = callback
    callback = null
    previousCallback?.onCustomViewHidden()
    previousOrientation?.let { context.activity()?.requestedOrientation = it }
    previousOrientation = null
    web.visibility = View.VISIBLE
    onFullscreen(false)
  }

  fun pause() {
    if (!destroyed) {
      web.evaluateJavascript(
        "document.querySelectorAll('iframe').forEach(function(f){f.contentWindow.postMessage(JSON.stringify({event:'command',func:'pauseVideo',args:[]}),'https://www.youtube.com');f.contentWindow.postMessage(JSON.stringify({method:'pause'}),'https://player.vimeo.com');});",
        null,
      )
      web.onPause()
    }
  }

  fun resume() {
    if (!destroyed) web.onResume()
  }

  fun destroy() {
    if (destroyed) return
    pause()
    exitFullscreen()
    destroyed = true
    web.stopLoading()
    removeAllViews()
    web.destroy()
  }
}
