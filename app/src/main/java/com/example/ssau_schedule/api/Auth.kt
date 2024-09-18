package com.example.ssau_schedule.api

import android.content.Context
import com.example.ssau_schedule.BuildConfig
import com.example.ssau_schedule.R
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import kotlin.coroutines.suspendCoroutine

enum class AuthErrorMessage(private val resource: Int?) {
    LOGIN_IS_TOO_SHORT(R.string.login_is_too_short),
    PASSWORD_IS_TOO_SHORT(R.string.password_is_too_short),
    INCORRECT_LOGIN_OR_PASSWORD(R.string.incorrect_login_or_password);

    fun getMessage(context: Context) =
        if(resource != null) context.getString(resource) else null
}

class AuthorizationAPI(private var http: Http) {
    private val getAuthToken =
        { response: Response? -> response?.headers?.toMap()?.containsKey("set-cookie") == true }

    suspend fun signIn(
        login: String, password: String,
    ): Pair<String?, HttpRequestException?> {
        val (response, exception) = http.request(
            Method.POST,
            BuildConfig.SIGN_IN_URL,
            JSONArray(
                arrayOf(
                    mapOf(
                        Pair("login", login),
                        Pair("password", password)
                    )
                )
            ).toString().toRequestBody("application/json".toMediaType()),
            mapOf(
                Pair("Next-Action", "b395d17834d8b7df06372cbf1f241170a272d540")
            ).toHeaders())
        val token = if(response?.headers?.toMap()?.containsKey("set-cookie") == true)
            response.headers("set-cookie").joinToString(", ") else null
        return Pair(token, exception)
    }
}