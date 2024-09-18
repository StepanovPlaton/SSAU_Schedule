package com.example.ssau_schedule.api

import android.content.Context
import com.example.ssau_schedule.BuildConfig
import com.example.ssau_schedule.R
import com.example.ssau_schedule.Utils
import com.example.ssau_schedule.data.store.RawYear
import kotlinx.serialization.SerializationException
import okhttp3.Headers.Companion.toHeaders

enum class YearAPIErrorMessage(private val resource: Int?) {
    FAILED_GET_YEARS(R.string.failed_get_years),

    USER_NOT_AUTHORIZED(null);

    fun getMessage(context: Context) =
        if(resource != null) context.getString(resource) else null
}

class YearAPI(private var http: Http) {
    suspend fun getYears(
        token: String,
    ): Pair<List<RawYear>?, YearAPIErrorMessage?> {
        val (response) = http.request(
            Method.GET,
            BuildConfig.YEARS_URL,
            mapOf(
                Pair("Cookie", token)
            ).toHeaders())
        if(response?.code == 401) return Pair(null, YearAPIErrorMessage.USER_NOT_AUTHORIZED)
        if(response?.body == null) return Pair(null, YearAPIErrorMessage.FAILED_GET_YEARS)
        try {
            val rawYears = Utils.Serializer
                .decodeFromString<List<RawYear>>(response.body!!.string())
            return if(rawYears.isNotEmpty()) Pair(rawYears, null)
            else Pair(null, YearAPIErrorMessage.FAILED_GET_YEARS)
        }
        catch(e: SerializationException) {
            return Pair(null, YearAPIErrorMessage.FAILED_GET_YEARS)
        }
        catch (e: IllegalArgumentException) {
            return Pair(null, YearAPIErrorMessage.FAILED_GET_YEARS)
        }
    }
}