package com.example.ssau_schedule.api

import android.content.Context
import com.example.ssau_schedule.BuildConfig
import com.example.ssau_schedule.R
import com.example.ssau_schedule.data.store.AuthStore
import kotlinx.coroutines.CoroutineScope
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray

enum class AuthErrorMessage(private val resource: Int) {
    LOGIN_IS_TOO_SHORT(R.string.login_is_too_short),
    PASSWORD_IS_TOO_SHORT(R.string.password_is_too_short),
    INCORRECT_LOGIN_OR_PASSWORD(R.string.incorrect_login_or_password);

    fun getMessage(context: Context) =
        context.getString(resource)
}

class AuthorizationAPI(
    private var http: Http,
    private var context: Context,
    private var scope: CoroutineScope
) {
    private val responseHasAuthToken =
        { response: Response? -> response?.headers?.toMap()?.containsKey("set-cookie") == true }

    fun signIn(
        login: String, password: String,
        callback: (token: String) -> Unit,
        exceptionCallback: ((error: HttpRequestException) -> Unit)? = null
    ) {
        http.request(
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
            ).toHeaders(),
            fun(response) {
                if (responseHasAuthToken(response)) {
                    val token = response.headers("set-cookie").joinToString(", ")
                    AuthStore.setAuthToken(token, context, scope) { callback(token) }
                } else
                    exceptionCallback?.invoke(
                        HttpRequestException("Authorization token not found"))
            },
            fun(error, response): Boolean {
                if (responseHasAuthToken(response)) return true
                exceptionCallback?.invoke(error)
                return false
            }
        )
    }


}