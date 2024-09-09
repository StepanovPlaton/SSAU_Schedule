package com.example.ssau_schedule.data.store

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

val Context.generalStore by preferencesDataStore(name = "user")

@Serializable
data class User(val name: String)

@Serializable
data class Group(val id: Int, val name: String)

data class Year(
    val id: Int,
    val startDate: Date,
    val endDate: Date,
) {
    companion object {
        @SuppressLint("SimpleDateFormat")
        val DateFormat = SimpleDateFormat("yyyy-MM-dd")

        fun parseDate(dateString: String): Date = DateFormat.parse(dateString)!!
        fun dateFormat(date: Date): String = DateFormat.format(date)
    }

    fun hasDate(date: Date) = date.after(startDate) && endDate.after(date)
    fun getWeekOfDate(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.minimalDaysInFirstWeek = 6
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.time = startDate
        val firstWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        calendar.time = date
        return (calendar.get(Calendar.WEEK_OF_YEAR) - firstWeek)+1
    }

}

@Serializable
data class RawYear(
    val id: Int,
    val startDate: String,
    val endDate: String,
    val isCurrent: Boolean,
) {
    @SuppressLint("SimpleDateFormat")
    fun toYear() = Year(
        id = id,
        startDate = Year.parseDate(startDate),
        endDate = Year.parseDate(endDate),
    )
}

class GeneralStore {
    class Keys {
        companion object {
            val CURRENT_GROUP_ID = intPreferencesKey("group_id")
            val CURRENT_GROUP_NAME = stringPreferencesKey("group_name")

            val CURRENT_YEAR_ID = intPreferencesKey("year_id")
            val CURRENT_YEAR_START = stringPreferencesKey("year_start")
            val CURRENT_YEAR_END = stringPreferencesKey("year_end")
        }
    }

    companion object {
        fun setCurrentGroup(
            group: Group,
            context: Context,
            scope: CoroutineScope,
            callback: (() -> Unit)? = null
        ) {
            scope.launch {
                context.generalStore.edit { generalStore ->
                    generalStore[Keys.CURRENT_GROUP_ID] = group.id
                    generalStore[Keys.CURRENT_GROUP_NAME] = group.name
                }.run { callback?.invoke() }
            }
        }

        fun getCurrentGroup(
            context: Context,
            scope: CoroutineScope,
            callback: (group: Group?) -> Unit
        ) {
            scope.launch {
                val currentGroupId = context.generalStore.data
                    .map { generalStore -> generalStore[Keys.CURRENT_GROUP_ID] }.first()
                val currentGroupName = context.generalStore.data
                    .map { generalStore -> generalStore[Keys.CURRENT_GROUP_NAME] }.first()
                callback(
                    if(currentGroupId != null && currentGroupName != null)
                        Group(id = currentGroupId,
                            name = currentGroupName)
                    else null
                )
            }
        }

        fun setCurrentYear(
            year: Year,
            context: Context,
            scope: CoroutineScope,
            callback: (() -> Unit)? = null
        ) {
            scope.launch {
                context.generalStore.edit { generalStore ->
                    generalStore[Keys.CURRENT_YEAR_ID] = year.id
                    generalStore[Keys.CURRENT_YEAR_START] = Year.dateFormat(year.startDate)
                    generalStore[Keys.CURRENT_YEAR_END] = Year.dateFormat(year.endDate)
                }.run { callback?.invoke() }
            }
        }

        fun getCurrentYear(
            context: Context,
            scope: CoroutineScope,
            callback: (year: Year?) -> Unit
        ) {
            scope.launch {
                val currentYearId = context.generalStore.data
                    .map { generalStore -> generalStore[Keys.CURRENT_YEAR_ID] }.first()
                val currentYearStartDate = context.generalStore.data
                    .map { generalStore -> generalStore[Keys.CURRENT_YEAR_START]
                    }.first()
                val currentYearEndDate = context.generalStore.data
                    .map { generalStore -> generalStore[Keys.CURRENT_YEAR_END]
                    }.first()
                callback(
                    if(currentYearId != null &&
                            currentYearStartDate != null &&
                            currentYearEndDate != null)
                        Year(id = currentYearId,
                            startDate = Year.parseDate(currentYearStartDate),
                            endDate = Year.parseDate(currentYearEndDate))
                    else null
                )
            }
        }
    }


}