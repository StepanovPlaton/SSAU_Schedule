package com.example.ssau_schedule.data.store

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ssau_schedule.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.util.Calendar
import java.util.Date

val Context.yearStore by preferencesDataStore(name = "year")

data class Year(
    val id: Int,
    val startDate: Date,
    val endDate: Date,
) {
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
        startDate = Utils.Date.parse(startDate),
        endDate = Utils.Date.parse(endDate),
    )
}

class YearStore {
    class Keys {
        companion object {
            val CURRENT_YEAR_ID = intPreferencesKey("year_id")
            val CURRENT_YEAR_START = stringPreferencesKey("year_start")
            val CURRENT_YEAR_END = stringPreferencesKey("year_end")
        }
    }
    companion object {
        suspend fun setCurrentYear(
            year: Year,
            context: Context,
        ) {
            context.yearStore.edit { yearStore ->
                yearStore[Keys.CURRENT_YEAR_ID] = year.id
                yearStore[Keys.CURRENT_YEAR_START] = Utils.Date.storeFormat(year.startDate)
                yearStore[Keys.CURRENT_YEAR_END] = Utils.Date.storeFormat(year.endDate)
            }
        }

        fun setCurrentYear(
            year: Year,
            context: Context,
            scope: CoroutineScope,
            callback: (() -> Unit)? = null
        ) = scope.launch { setCurrentYear(year, context) }.run { callback?.invoke() }

        suspend fun getCurrentYear(context: Context): Year? {
            val currentYearId = context.yearStore.data
                .map { yearStore -> yearStore[Keys.CURRENT_YEAR_ID] }.first()
            val currentYearStartDate = context.yearStore.data
                .map { yearStore -> yearStore[Keys.CURRENT_YEAR_START]
                }.first()
            val currentYearEndDate = context.yearStore.data
                .map { yearStore -> yearStore[Keys.CURRENT_YEAR_END]
                }.first()
            return if(currentYearId != null &&
                currentYearStartDate != null &&
                currentYearEndDate != null)
                Year(id = currentYearId,
                    startDate = Utils.Date.parse(currentYearStartDate),
                    endDate = Utils.Date.parse(currentYearEndDate))
            else null
        }

        fun getCurrentYear(
            context: Context,
            scope: CoroutineScope,
            callback: (year: Year?) -> Unit
        ) = scope.launch { callback(getCurrentYear(context)) }
    }
}