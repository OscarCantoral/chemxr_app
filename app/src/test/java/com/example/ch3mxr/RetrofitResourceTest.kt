package com.example.ch3mxr

import com.example.ch3mxr.data.remote.ResourceService
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.*
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Retrofit

class RetrofitResourceTest {
  @Test
  fun streamsAbsoluteResourceUrlWithoutInventedApiRoutes() = runBlocking {
    val server = MockWebServer()
    server.start()
    try {
      server.enqueue(
        MockResponse().setHeader("Content-Type", "application/pdf").setBody("%PDF-test")
      )
      val api =
        Retrofit.Builder()
          .baseUrl(server.url("/unused/"))
          .build()
          .create(ResourceService::class.java)
      val response = api.download(server.url("/courses/chemistry/manual.pdf").toString())
      assertTrue(response.isSuccessful)
      assertEquals("%PDF-test", response.body()!!.use { it.string() })
      assertEquals("/courses/chemistry/manual.pdf", server.takeRequest().path)
    } finally {
      server.shutdown()
    }
  }
}
