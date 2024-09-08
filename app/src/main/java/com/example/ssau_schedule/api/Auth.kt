package com.example.ssau_schedule.api

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.preferences.core.edit
import com.example.ssau_schedule.BuildConfig
import com.example.ssau_schedule.R
import com.example.ssau_schedule.data.source.AuthStoreKeys
import com.example.ssau_schedule.data.source.authStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import org.json.JSONArray

enum class AuthErrorMessage(private val resource: Int) {
    LOGIN_IS_TOO_SHORT(R.string.login_is_too_short),
    PASSWORD_IS_TOO_SHORT(R.string.password_is_too_short),
    INCORRECT_LOGIN_OR_PASSWORD(R.string.incorrect_login_or_password);

    fun getMessage(context: Context) =
        context.getString(resource)
}

class AuthorizationAPI(private var http: Http, private var context: Context) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: AuthorizationAPI? = null
        fun getInstance(http: Http, context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthorizationAPI(http, context).also {
                    INSTANCE = it
                }
            }
    }

    private val responseHasAuthToken =
        {response: Response? -> response?.headers?.toMap()?.containsKey("set-cookie") == true}

    private fun safeAuthToken(response: Response,
                              authScope: CoroutineScope,
                              callback: HttpResponseCallback) {
        authScope.launch {
            context.authStore.edit { authStore ->
                authStore[AuthStoreKeys.AUTH_TOKEN] =
                    response.headers("set-cookie").joinToString(", ")
            }.run {
                callback(response)
            }
        }
    }

    fun signIn(login: String, password: String, authScope: CoroutineScope,
               callback: HttpResponseCallback,
               exceptionCallback: HttpExceptionCallback? = null) {
        http.request(
            Method.POST,
            BuildConfig.SIGN_IN_URL,
            JSONArray(arrayOf(mapOf(
                Pair("login", login),
                Pair("password", password)
            ))).toString().toRequestBody("application/json".toMediaType()),
            mapOf(
                Pair("Next-Action", "b395d17834d8b7df06372cbf1f241170a272d540")
            ).toHeaders(),
            fun(response) {
                if(responseHasAuthToken(response))
                    safeAuthToken(response, authScope, callback)
                else if(exceptionCallback != null)
                    exceptionCallback(IOException("Authorization token not found"), response)
            },
            fun (error, response): Boolean {
                if(responseHasAuthToken(response)) return true
                if(exceptionCallback !== null) exceptionCallback(error, response)
                return false
            }
        )
    }




}