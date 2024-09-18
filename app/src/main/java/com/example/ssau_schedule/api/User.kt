package com.example.ssau_schedule.api

import android.content.Context
import com.example.ssau_schedule.BuildConfig
import com.example.ssau_schedule.R
import com.example.ssau_schedule.Utils
import com.example.ssau_schedule.data.unsaved.User
import kotlinx.serialization.SerializationException
import okhttp3.Headers.Companion.toHeaders

enum class UserAPIErrorMessage(private val resource: Int?) {
    FAILED_GET_USER_DETAILS(R.string.failed_get_user_details),

    USER_NOT_AUTHORIZED(null);

    fun getMessage(context: Context) =
        if(resource != null) context.getString(resource) else null
}

class UserAPI(private var http: Http) {
    suspend fun getUserDetails(
        token: String,
    ): Pair<User?, UserAPIErrorMessage?> {
        val (response) = http.request(
            Method.GET,
            BuildConfig.USER_DETAILS_URL,
            mapOf(
                Pair("Cookie", token)
            ).toHeaders())
        if(response?.code == 401) return Pair(null, UserAPIErrorMessage.USER_NOT_AUTHORIZED)
        if(response?.body == null) return Pair(null, UserAPIErrorMessage.FAILED_GET_USER_DETAILS)
        return try {
            Pair(Utils.Serializer.decodeFromString<User>(response.body!!.string()), null)
        } catch(e: SerializationException) {
            Pair(null, UserAPIErrorMessage.FAILED_GET_USER_DETAILS)
        } catch (e: IllegalArgumentException) {
            Pair(null, UserAPIErrorMessage.FAILED_GET_USER_DETAILS)
        }
    }
}