package com.example.ssau_schedule.api

import android.content.Context
import android.util.Log
import com.example.ssau_schedule.BuildConfig
import com.example.ssau_schedule.R
import com.example.ssau_schedule.data.store.AuthStore
import com.example.ssau_schedule.data.store.Group
import com.example.ssau_schedule.data.store.RawYear
import com.example.ssau_schedule.data.store.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.Headers.Companion.toHeaders


enum class ApiErrorMessage(private val resource: Int?) {
    FAILED_GET_USER_DETAILS(R.string.failder_get_user_details),
    NOT_MEMBER_OF_ANY_GROUP(R.string.not_member_of_any_group),
    FAILED_GET_USER_GROUPS(R.string.failed_get_user_groups),
    FAILED_GET_YEARS(R.string.failed_get_years),

    USER_NOT_AUTHORIZED(null);

    fun getMessage(context: Context) =
        if(resource != null) context.getString(resource) else null
}

class GeneralApi(
    private var http: Http,
    private var context: Context,
    private var scope: CoroutineScope
) {
    fun getUserDetails(
        token: String,
        callback: (user: User) -> Unit,
        exceptionCallback: ((error: ApiErrorMessage) -> Unit)
    ) {
        http.request(
            Method.GET,
            BuildConfig.USER_DETAILS_URL,
            mapOf(
                Pair("Cookie", token)
            ).toHeaders(),
            { response ->
                try {
                    if(response.body != null) {
                        val serializer = Json {
                            isLenient = true
                            ignoreUnknownKeys = true
                        }
                        callback(serializer
                            .decodeFromString<User>(response.body!!.string()))
                    }
                    else exceptionCallback(ApiErrorMessage.FAILED_GET_USER_DETAILS)
                }
                catch(e: SerializationException) {
                    Log.e("Groups Deserialization exception", e.message ?: "")
                    exceptionCallback(ApiErrorMessage.FAILED_GET_USER_DETAILS)
                }
                catch (e: IllegalArgumentException) {
                    Log.e("Groups argument exception", e.message ?: "")
                    exceptionCallback(ApiErrorMessage.FAILED_GET_USER_DETAILS)
                }
            },
            fun(_, r): Boolean {
                if(r?.code == 401) exceptionCallback(ApiErrorMessage.USER_NOT_AUTHORIZED)
                else exceptionCallback(ApiErrorMessage.FAILED_GET_USER_DETAILS)
                return false
            }
        )
    }
    fun getUserDetails(
        callback: (user: User) -> Unit,
        exceptionCallback: ((error: ApiErrorMessage) -> Unit)
    ) {
        AuthStore.getAuthToken(context, scope) { authToken ->
            if(authToken != null)
                getUserDetails(authToken, callback, exceptionCallback)
            else exceptionCallback(ApiErrorMessage.USER_NOT_AUTHORIZED)
        }
    }

    fun getUserGroups(
        token: String,
        callback: (groups: List<Group>) -> Unit,
        exceptionCallback: (error: ApiErrorMessage) -> Unit
    ) {
        http.request(
            Method.GET,
            BuildConfig.USER_GROUPS_URL,
            mapOf(
                Pair("Cookie", token)
            ).toHeaders(),
            { response ->
                try {
                    if(response.body != null) {
                        val serializer = Json {
                            isLenient = true
                            ignoreUnknownKeys = true
                        }
                        val groups = serializer
                            .decodeFromString<List<Group>>(response.body!!.string())
                        if(groups.isNotEmpty()) callback(groups)
                        else { exceptionCallback(ApiErrorMessage.NOT_MEMBER_OF_ANY_GROUP) }
                    }
                    else exceptionCallback(ApiErrorMessage.FAILED_GET_USER_GROUPS)
                }
                catch(e: SerializationException) {
                    Log.e("Groups Deserialization exception", e.message ?: "")
                    exceptionCallback(ApiErrorMessage.FAILED_GET_USER_GROUPS)
                }
                catch (e: IllegalArgumentException) {
                    Log.e("Groups argument exception", e.message ?: "")
                    exceptionCallback(ApiErrorMessage.FAILED_GET_USER_GROUPS)
                }
            },
            fun(_, r): Boolean {
                if(r?.code == 401) exceptionCallback(ApiErrorMessage.USER_NOT_AUTHORIZED)
                else exceptionCallback(ApiErrorMessage.FAILED_GET_USER_GROUPS)
                return false
            }
        )
    }

    fun getUserGroups(
        callback: (groups: List<Group>) -> Unit,
        exceptionCallback: (error: ApiErrorMessage) -> Unit
    ) {
        AuthStore.getAuthToken(context, scope) { authToken ->
            if(authToken != null)
                getUserGroups(authToken, callback, exceptionCallback)
            else exceptionCallback(ApiErrorMessage.USER_NOT_AUTHORIZED)
        }
    }

    fun getYears(
        token: String,
        callback: (rawYears: List<RawYear>) -> Unit,
        exceptionCallback: (error: ApiErrorMessage) -> Unit
    ) {
        http.request(
            Method.GET,
            BuildConfig.YEARS_URL,
            mapOf(
                Pair("Cookie", token)
            ).toHeaders(),
            { response ->
                try {
                    if(response.body != null) {
                        val serializer = Json {
                            isLenient = true
                            ignoreUnknownKeys = true
                        }
                        val rawYears = serializer
                            .decodeFromString<List<RawYear>>(response.body!!.string())
                        if(rawYears.isNotEmpty()) callback(rawYears)
                        else { exceptionCallback(ApiErrorMessage.FAILED_GET_YEARS) }
                    }
                    else exceptionCallback(ApiErrorMessage.FAILED_GET_YEARS)
                }
                catch(e: SerializationException) {
                    Log.e("Groups Deserialization exception", e.message ?: "")
                    exceptionCallback(ApiErrorMessage.FAILED_GET_YEARS)
                }
                catch (e: IllegalArgumentException) {
                    Log.e("Groups argument exception", e.message ?: "")
                    exceptionCallback(ApiErrorMessage.FAILED_GET_YEARS)
                }
            },
            fun(_, r): Boolean {
                if(r?.code == 401) exceptionCallback(ApiErrorMessage.USER_NOT_AUTHORIZED)
                else exceptionCallback(ApiErrorMessage.FAILED_GET_YEARS)
                return false
            }
        )
    }

    fun getYears(
        callback: (rawYears: List<RawYear>) -> Unit,
        exceptionCallback: (error: ApiErrorMessage) -> Unit
    ) {
        AuthStore.getAuthToken(context, scope) { authToken ->
            if(authToken != null)
                getYears(authToken, callback, exceptionCallback)
            else exceptionCallback(ApiErrorMessage.USER_NOT_AUTHORIZED)
        }
    }
}