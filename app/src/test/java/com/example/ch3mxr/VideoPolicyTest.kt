package com.example.ch3mxr

import com.example.ch3mxr.core.media.*
import org.junit.Assert.*
import org.junit.Test

class VideoPolicyTest {
  @Test
  fun convertsOnlyRecognizedProvidersAndValidIds() {
    assertTrue(VideoSources.parse("https://youtu.be/oPrXErLylxA") is VideoSource.Web)
    assertTrue(VideoSources.parse("https://www.youtube.com/watch?v=oPrXErLylxA") is VideoSource.Web)
    assertTrue(VideoSources.parse("https://vimeo.com/123456") is VideoSource.Web)
    assertNull(VideoSources.parse("https://youtube.com.evil.test/watch?v=oPrXErLylxA"))
    assertNull(VideoSources.parse("https://youtube.com@evil.test/watch?v=oPrXErLylxA"))
    assertNull(VideoSources.parse("http://youtube.com/watch?v=oPrXErLylxA"))
    assertNull(VideoSources.parse("https://www.youtube.com:444/watch?v=oPrXErLylxA"))
    assertNull(VideoSources.parse("https://www.youtube.com/watch?v=<script>"))
  }

  @Test
  fun externalAndLocalVideosAreClassifiedSeparately() {
    assertTrue(VideoSources.parse("https://media.example.test/lesson.mp4") is VideoSource.File)
    assertTrue(VideoSources.parse("content://documents/video/1") is VideoSource.File)
    assertFalse(VideoSources.mayNavigate("file:///data/local/private.html"))
    assertFalse(VideoSources.mayNavigate("https://evil.test/player"))
    assertTrue(VideoSources.mayNavigate("https://player.vimeo.com/video/1234"))
  }
}
