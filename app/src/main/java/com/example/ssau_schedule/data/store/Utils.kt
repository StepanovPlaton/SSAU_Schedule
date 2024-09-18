package com.example.ssau_schedule.data.store

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class GeneralData(
    val token: String,
    val group: Group,
    val year: Year
)

class StoreUtils {
    companion object {
        suspend fun getGeneralData(
            context: Context,
        ): GeneralData? {
            val token = AuthStore.getAuthToken(context)
            val group = GroupStore.getCurrentGroup(context)
            val year = YearStore.getCurrentYear(context)
            return if (token != null && group != null && year != null)
                GeneralData(token, group, year)
            else null
        }

        fun getGeneralData(
            context: Context,
            scope: CoroutineScope,
            callback: (data: GeneralData?) -> Unit,
        ) = scope.launch { callback(getGeneralData(context)) }
    }
}