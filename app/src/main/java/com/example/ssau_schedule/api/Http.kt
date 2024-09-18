package com.example.ssau_schedule.api

import android.util.Log
import com.example.ssau_schedule.BuildConfig
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

enum class Method {
    GET,
    POST,
    PUT,
    DELETE
}

typealias HttpRequestException = IOException

class Http {
    val http = OkHttpClient()

    suspend fun request(
        method: Method,
        url: String,
        body: RequestBody? = null,
        headers: Headers? = null,
    ): Pair<Response?, HttpRequestException?> {
        val request =
            Request.Builder().url(BuildConfig.BASE_URL + url).method(method.toString(), body)
        if (headers !== null) request.headers(headers)
        return suspendCoroutine { coroutine ->
            http.newCall(request.build()).enqueue(object : Callback {
                override fun onFailure(call: Call, e: HttpRequestException) {
                    Log.e("Http request failed", e.toString())
                    coroutine.resume(Pair(null, e))
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful)
                        coroutine.resume(Pair(response,
                            HttpRequestException("Http response is not successful")))
                    else coroutine.resume(Pair(response, null))
                }
            })
        }

    }

    suspend fun request(
        method: Method,
        url: String,
        headers: Headers? = null,
    ) = request(method, url, null, headers)

    suspend fun request(
        method: Method,
        url: String,
    ) = request(method, url, null, null)
}