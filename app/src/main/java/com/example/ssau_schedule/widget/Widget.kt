package com.example.ssau_schedule.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.example.ssau_schedule.R
import com.example.ssau_schedule.Utils
import com.example.ssau_schedule.data.base.Database
import com.example.ssau_schedule.data.base.entity.lesson.Lesson
import com.example.ssau_schedule.ui.theme.LessonColors
import com.example.ssau_schedule.ui.theme.SSAU_ScheduleWidgetTheme
import java.text.SimpleDateFormat
import java.util.Date

class ScheduleWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            SSAU_ScheduleWidgetTheme {
                WidgetContent(context)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Composable
    private fun WidgetContent(context: Context) {
        val database = remember { Database.getInstance(context) }
        val lessons = remember { mutableStateOf<List<Lesson>>(listOf()) }
        LaunchedEffect(false) {
            lessons.value = database.lessonDao().getAll()
        }

        Box(GlanceModifier.fillMaxSize().background(GlanceTheme.colors.surface)) {
            LazyColumn {
                items(7) {
                    Column {
                        val todayLessons = lessons.value.filter { lesson ->
                            lesson.dayOfWeek - 1 == Utils.Date.getDayOfWeek(
                                Utils.Date.addDays(
                                    Date(),
                                    it
                                )
                            ) &&
                                    lesson.week - 1 == Utils.Date.getWeekOfStudyYear(Date())
                        }.sortedBy { lesson -> lesson.beginTime }

                        Box(
                            GlanceModifier.fillMaxWidth()
                                .background(LessonColors.Background.Dark.Unknown)
                                .padding(20.dp, 10.dp).cornerRadius(12.dp)
                        ) {
                            Text(
                                SimpleDateFormat("d MMMM").format(Utils.Date.addDays(
                                    Date(),
                                    it
                                )) + if(todayLessons.isEmpty()) " - "+context.getString(R.string.no_classes) else "",
                                modifier = GlanceModifier.fillMaxWidth(),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Left,
                                    color = GlanceTheme.colors.tertiary
                                )
                            )
                        }

                        if(todayLessons.isNotEmpty()) LessonCards(todayLessons)
                        Spacer(GlanceModifier.fillMaxWidth().height(10.dp))
                    }

                }
            }
        }

    }
}

class WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ScheduleWidget()
}

