package com.example.ch3mxr.data.remote

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/** Absolute authorized resource URLs; no guessed backend endpoint or embedded client secret. */
interface ResourceService {
  @Streaming @GET suspend fun download(@Url url: String): Response<ResponseBody>
}
