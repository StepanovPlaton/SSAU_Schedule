package com.example.ssau_schedule.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ssau_schedule.data.base.entity.lesson.Lesson
import com.example.ssau_schedule.ui.theme.LessonColors

@Composable
fun LessonCard(modifier: Modifier, lesson: Lesson) {
    Row(modifier.fillMaxWidth()
        .height(130.dp).padding(14.dp, 8.dp)
        .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp))
        .background(
            if(isSystemInDarkTheme())
                lesson.type?.darkBackground ?: LessonColors.Background.Dark.Unknown
            else lesson.type?.lightBackground ?: LessonColors.Background.Light.Unknown
        )
    ) {
        Box(modifier.fillMaxHeight().width(16.dp).shadow(4.dp)
            .background(lesson.type?.foreground ?: LessonColors.Foreground.Unknown))
        Column(modifier.fillMaxHeight().padding(10.dp, 10.dp),
            verticalArrangement = Arrangement.SpaceBetween) {
            Row(modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${lesson.beginTime} - ${lesson.endTime}",
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.bodyMedium)
                Text("512 - 5",
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.bodyMedium)
            }
            AutoResizeText(lesson.discipline,
                modifier = modifier.fillMaxWidth(),
                fontSizeRange = FontSizeRange(10.sp, 24.sp),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleLarge)
            Text(lesson.teacher,
                modifier = modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
fun LessonCards(lessons: List<Lesson>) {
    Column(Modifier.verticalScroll(ScrollState(0))) {
        lessons.forEach { lesson ->
            LessonCard(Modifier, lesson)
        }
    }
}

