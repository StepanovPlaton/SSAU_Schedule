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
import java.io.IOException

enum class Method {
    GET,
    POST,
    PUT,
    DELETE
}

typealias HttpResponseCallback = (response: Response) -> Unit
typealias HttpExceptionVerifyCallback = (exception: IOException, response: Response?) -> Boolean
typealias HttpExceptionCallback = (exception: IOException, response: Response?) -> Unit

class Http {
    val http = OkHttpClient()

    fun request(
        method: Method,
        url: String,
        body: RequestBody? = null,
        headers: Headers? = null,
        callback: HttpResponseCallback,
        exceptionCallback: HttpExceptionVerifyCallback? = null
    ) {
        val request = Request.Builder().url(BuildConfig.BASE_URL+url).
                                                method(method.toString(), body)
        if(headers !== null) request.headers(headers)
        http.newCall(request.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Http request failed", e.toString())
                if(exceptionCallback !== null) exceptionCallback(e, null)
            }

            override fun onResponse(call: Call, response: Response) {
                var runCallback = false
                if (!response.isSuccessful && exceptionCallback !== null)
                    runCallback = exceptionCallback(
                        IOException("Http response is not successful"), response)
                if(runCallback || response.isSuccessful) callback(response)
            }
        })
    }
}