package com.example.ssau_schedule.api

import android.content.Context
import android.util.Log
import com.example.ssau_schedule.BuildConfig
import com.example.ssau_schedule.R
import com.example.ssau_schedule.Utils
import com.example.ssau_schedule.data.unsaved.APILessons
import com.example.ssau_schedule.data.store.Group
import com.example.ssau_schedule.data.store.Year
import kotlinx.serialization.SerializationException
import okhttp3.Headers.Companion.toHeaders

enum class LessonAPIErrorMessage(private val resource: Int?) {
    FAILED_GET_LESSONS(R.string.failed_get_lessons),

    USER_NOT_AUTHORIZED(null);

    fun getMessage(context: Context) =
        if(resource != null) context.getString(resource) else null
}

class LessonAPI(private var http: Http) {
    suspend fun getLessons(
        token: String,
        group: Group,
        year: Year,
        week: Int,
    ): Pair<APILessons?, LessonAPIErrorMessage?> {
        val (response) = http.request(
            Method.GET,
            "${BuildConfig.LESSONS_URL}?yearId=${year.id}"+
                    "&week=$week&userType=student&groupId=${group.id}",
            mapOf(
                Pair("Cookie", token)
            ).toHeaders())
        if(response?.code == 401) return Pair(null, LessonAPIErrorMessage.USER_NOT_AUTHORIZED)
        if(response?.body == null) return Pair(null, LessonAPIErrorMessage.FAILED_GET_LESSONS)
        try { return Pair(Utils.Serializer
            .decodeFromString<APILessons>(response.body!!.string()), null)
        } catch(e: SerializationException) {
            Log.e("Serialization error", e.message.toString())
            return Pair(null, LessonAPIErrorMessage.FAILED_GET_LESSONS)
        } catch (e: IllegalArgumentException) {
            Log.e("Serialization error", e.message.toString())
            return Pair(null, LessonAPIErrorMessage.FAILED_GET_LESSONS)
        }
    }
}