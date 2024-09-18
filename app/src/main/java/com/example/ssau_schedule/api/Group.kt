package com.example.ssau_schedule.api

import android.content.Context
import com.example.ssau_schedule.BuildConfig
import com.example.ssau_schedule.R
import com.example.ssau_schedule.Utils
import com.example.ssau_schedule.data.store.Group
import kotlinx.serialization.SerializationException
import okhttp3.Headers.Companion.toHeaders

enum class GroupAPIErrorMessage(private val resource: Int?) {
    NOT_MEMBER_OF_ANY_GROUP(R.string.not_member_of_any_group),
    FAILED_GET_USER_GROUPS(R.string.failed_get_user_groups),

    USER_NOT_AUTHORIZED(null);

    fun getMessage(context: Context) =
        if(resource != null) context.getString(resource) else null
}

class GroupAPI(private var http: Http) {
    suspend fun getUserGroups(token: String): Pair<List<Group>?, GroupAPIErrorMessage?> {
        val (response) = http.request(
            Method.GET,
            BuildConfig.USER_GROUPS_URL,
            mapOf(
                Pair("Cookie", token)
            ).toHeaders())
        if(response?.code == 401) return Pair(null, GroupAPIErrorMessage.USER_NOT_AUTHORIZED)
        if(response?.body == null) return Pair(null, GroupAPIErrorMessage.FAILED_GET_USER_GROUPS)
        else {
            try {
                val groups = Utils.Serializer
                    .decodeFromString<List<Group>>(response.body!!.string())
                return if (groups.isNotEmpty()) Pair(groups, null)
                else Pair(null, GroupAPIErrorMessage.NOT_MEMBER_OF_ANY_GROUP)
            } catch (e: SerializationException) {
                return Pair(null, GroupAPIErrorMessage.FAILED_GET_USER_GROUPS)
            } catch (e: IllegalArgumentException) {
                return Pair(null, GroupAPIErrorMessage.FAILED_GET_USER_GROUPS)
            }
        }
    }
}