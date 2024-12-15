package com.example.ssau_schedule.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.example.ssau_schedule.data.base.entity.lesson.Lesson
import com.example.ssau_schedule.ui.theme.LessonColors

@Composable
fun LessonCard(lesson: Lesson) {
    Row(
        GlanceModifier.fillMaxWidth()
            .cornerRadius(12.dp)
            .background(lesson.type?.darkBackground ?: LessonColors.Background.Dark.Unknown)
    ) {
        Box(
            GlanceModifier.fillMaxHeight().width(10.dp)
                .background(lesson.type?.foreground ?: LessonColors.Foreground.Unknown)
        ) { }
        Column(GlanceModifier.fillMaxHeight().padding(10.dp, 10.dp)) {
            Text(
                "${lesson.beginTime} - ${lesson.endTime}, ${lesson.room ?: "???"} - ${lesson.building ?: "?"}",
                style = TextStyle(
                    textAlign = TextAlign.Start,
                    fontSize = 14.sp, color = GlanceTheme.colors.tertiary
                )
            )
            Spacer(GlanceModifier.fillMaxWidth().height(4.dp))
            Text(
                lesson.discipline,
                modifier = GlanceModifier.fillMaxWidth(),
                style = TextStyle(fontSize = 20.sp, color = GlanceTheme.colors.secondary)
            )
            Spacer(GlanceModifier.fillMaxWidth().height(4.dp))
            Text(
                lesson.teacher,
                modifier = GlanceModifier.fillMaxWidth(),
                style = TextStyle(fontSize = 12.sp, color = GlanceTheme.colors.tertiary)
            )
        }
    }
}

@Composable
fun LessonCards(lessons: List<Lesson>) {
    lessons.forEach { lesson ->
        LessonCard(lesson)
    }
}
