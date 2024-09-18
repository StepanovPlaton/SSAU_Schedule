package com.example.ssau_schedule.data.base.entity.lesson

import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ssau_schedule.ui.theme.LessonColors
import kotlinx.serialization.Serializable

enum class LessonType(
    val displayName: String,
    val foreground: Color,
    val darkBackground: Color,
    val lightBackground: Color,
) {
    LECTURE("Лекция",
        LessonColors.Foreground.Lecture,
        LessonColors.Background.Dark.Lecture,
        LessonColors.Background.Light.Lecture),
    PRACTICE("Практика",
        LessonColors.Foreground.Practice,
        LessonColors.Background.Dark.Practice,
        LessonColors.Background.Light.Practice),
    LABORATORY("Лабораторная",
        LessonColors.Foreground.Laboratory,
        LessonColors.Background.Dark.Laboratory,
        LessonColors.Background.Light.Laboratory),
    OTHER("Другое",
        LessonColors.Foreground.Other,
        LessonColors.Background.Dark.Other,
        LessonColors.Background.Light.Other),
    EXAMINATION("Экзамен",
        LessonColors.Foreground.Examination,
        LessonColors.Background.Dark.Examination,
        LessonColors.Background.Light.Examination),
    TEST("Зачёт",
        LessonColors.Foreground.Test,
        LessonColors.Background.Dark.Test,
        LessonColors.Background.Light.Test),
    CONSULTATION("Консультация",
        LessonColors.Foreground.Consultation,
        LessonColors.Background.Dark.Consultation,
        LessonColors.Background.Light.Consultation);

    companion object {
        fun getTypeFromName(name: String) =
            entries.firstOrNull() { it.displayName == name }
    }
}

@Serializable
@Entity(tableName = "lessons")
data class Lesson(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "type") val type: LessonType?,
    @ColumnInfo(name = "discipline") val discipline: String,
    @ColumnInfo(name = "week") val week: Int,
    @ColumnInfo(name = "day_of_week") val dayOfWeek: Int,
    @ColumnInfo(name = "teacher") val teacher: String,
    @ColumnInfo(name = "begin_time") val beginTime: String,
    @ColumnInfo(name = "end_time") val endTime: String,
    @ColumnInfo(name = "conference_url") val conferenceUrl: String?,
)