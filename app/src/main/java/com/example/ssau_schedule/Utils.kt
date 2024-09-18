package com.example.ssau_schedule

import android.annotation.SuppressLint
import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Calendar

class Utils {
    companion object {
        val Serializer = Json {
            isLenient = true
            ignoreUnknownKeys = true
        }

        @Composable
        fun keyboardState(): State<Boolean> {
            val keyboardOpen = remember { mutableStateOf(false) }
            val view = LocalView.current
            DisposableEffect(view) {
                val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
                    val rect = Rect()
                    view.getWindowVisibleDisplayFrame(rect)
                    val screenHeight = view.rootView.height
                    val keypadHeight = screenHeight - rect.bottom
                    keyboardOpen.value = keypadHeight > screenHeight * 0.15
                }
                view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)
                onDispose { view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener) }
            }
            return keyboardOpen
        }
    }

    class Date {
        companion object {
            @SuppressLint("SimpleDateFormat")
            val StoreDateFormat = SimpleDateFormat("yyyy-MM-dd")
            @SuppressLint("SimpleDateFormat")
            val DateFormat = SimpleDateFormat("dd MMMM")

            fun parse(dateString: String): java.util.Date = StoreDateFormat.parse(dateString)!!
            fun storeFormat(date: java.util.Date): String = StoreDateFormat.format(date)
            fun format(date: java.util.Date): String = DateFormat.format(date)

            fun getDateOfWeek(data: java.util.Date): Int {
                val calendar = Calendar.getInstance()
                calendar.minimalDaysInFirstWeek = 6
                calendar.firstDayOfWeek = Calendar.MONDAY
                calendar.time = data
                return calendar.get(Calendar.DAY_OF_WEEK)-1
            }
        }
    }
}