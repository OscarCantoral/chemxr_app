package com.example.ch3mxr.ui.components

import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun VideoPlayerComponent(
    isOpened: Boolean,
    title: String = "VIDEO",
    videoUrl: String,
    onClose: () -> Unit
) {
    if (!isOpened) return

    val context = LocalContext.current
    val activity = context as? Activity

    var webView: WebView? by remember { mutableStateOf(null) }
    var customView: View? by remember { mutableStateOf(null) }
    var customViewCallback: WebChromeClient.CustomViewCallback? by remember { mutableStateOf(null) }
    var isFullscreen by remember { mutableStateOf(false) }

    fun exitFullscreen() {
        customViewCallback?.onCustomViewHidden()
        customView = null
        customViewCallback = null
        isFullscreen = false
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        webView?.visibility = View.VISIBLE
    }

    fun closePlayer() {
        exitFullscreen()
        webView?.stopLoading()
        webView?.clearHistory()
        webView?.clearCache(true)
        webView?.loadUrl("about:blank")
        onClose()
    }

    BackHandler {
        if (isFullscreen) {
            exitFullscreen()
        } else {
            closePlayer()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exitFullscreen()
            webView?.destroy()
        }
    }

    val embedUrl = remember(videoUrl) { toEmbedUrl(videoUrl) }

    if (isFullscreen && customView != null) {
        Dialog(
            onDismissRequest = { exitFullscreen() },
            properties = DialogProperties(
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                AndroidView(
                    factory = { _ ->
                        customView!!
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    Dialog(
        onDismissRequest = { closePlayer() },
        properties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF020617))
                .statusBarsPadding()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                color = Color(0xFF0B1220),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { closePlayer() },
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF0ED2F7)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar video",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            val appPackage = context.packageName
            val extraHeaders = mapOf("Referer" to "https://$appPackage")
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.mediaPlaybackRequiresUserGesture = false
                        settings.loadsImagesAutomatically = true
                        settings.allowFileAccess = true
                        settings.allowContentAccess = true
                        settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                        settings.setSupportZoom(true)
                        settings.builtInZoomControls = true
                        settings.displayZoomControls = false
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true
                        settings.setGeolocationEnabled(false)

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                                super.onShowCustomView(view, callback)
                                if (customView != null) {
                                    callback?.onCustomViewHidden()
                                    return
                                }
                                customView = view
                                customViewCallback = callback
                                isFullscreen = true
                                webView?.visibility = View.GONE
                                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                            }

                            override fun onHideCustomView() {
                                super.onHideCustomView()
                                exitFullscreen()
                            }
                        }

                        loadUrl(embedUrl,extraHeaders)
                        webView = this
                    }
                },
                update = { view ->
                    if (view.url != embedUrl) {
                        view.loadUrl(embedUrl,extraHeaders)
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            )
        }
    }
}

private fun extractYoutubeId(url: String): String? {
    val patterns = listOf(
        Regex("v=([^&]+)"),
        Regex("youtu\\.be/([^?&]+)"),
        Regex("youtube\\.com/embed/([^?&]+)"),
        Regex("shorts/([^?&]+)")
    )
    for (pattern in patterns) {
        val match = pattern.find(url)
        if (match != null) {
            return match.groupValues[1]
        }
    }
    return null
}

private fun toEmbedUrl(raw: String): String {
    val lower = raw.lowercase()
    if (lower.contains("youtube.com") || lower.contains("youtu.be")) {
        val id = extractYoutubeId(raw) ?: return raw
        return "https://www.youtube.com/embed/$id?autoplay=0&rel=0&modestbranding=1&playsinline=1&fs=1&enablejsapi=1"
    }
    if (lower.contains("vimeo.com")) {
        val id = Regex("vimeo\\.com/(\\d+)").find(raw)?.groupValues?.get(1)
        return if (id != null) {
            "https://player.vimeo.com/video/$id?title=0&byline=0&portrait=0&playsinline=1"
        } else {
            raw
        }
    }
    return raw
}