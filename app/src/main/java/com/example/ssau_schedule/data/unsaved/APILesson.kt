package com.example.ssau_schedule.data.unsaved

import android.content.Context
import com.example.ssau_schedule.R
import com.example.ssau_schedule.data.base.entity.lesson.Lesson
import com.example.ssau_schedule.data.base.entity.lesson.LessonType
import kotlinx.serialization.Serializable

enum class LessonConverterErrorMessage(private val resource: Int?) {
    NO_TEACHER_FOR_LESSON(R.string.failed_get_lessons),
    NO_DISCIPLINE_FOR_IET_LESSON(R.string.failed_get_lessons);

    fun getMessage(context: Context) =
        if(resource != null) context.getString(resource) else null
}

@Serializable data class APILessonType(val name: String)
@Serializable data class APILessonDiscipline(val name: String)
@Serializable data class APILessonTeacher(val name: String)
@Serializable data class APILessonTime(val beginTime: String, val endTime: String)
@Serializable data class APILessonConference(val url: String)
@Serializable data class APILessonFlow(val discipline: APILessonDiscipline)
@Serializable data class APILessonWeekDay(val id: Int)

@Serializable
data class APILesson(
    val id: Int,
    val type: APILessonType,
    val discipline: APILessonDiscipline,
    val teachers: List<APILessonTeacher>,
    val time: APILessonTime,
    val conference: APILessonConference?,
    val weekday: APILessonWeekDay
) {
    fun toLesson(week: Int): Pair<Lesson?, LessonConverterErrorMessage?> {
        return if(teachers.isEmpty()) Pair(null, LessonConverterErrorMessage.NO_TEACHER_FOR_LESSON)
        else Pair(Lesson(
            id = id,
            type = LessonType.getTypeFromName(type.name),
            discipline = discipline.name,
            teacher = teachers[0].name,
            beginTime = time.beginTime,
            endTime = time.endTime,
            conferenceUrl = conference?.url,
            dayOfWeek = weekday.id,
            week = week
        ), null)
    }

}

@Serializable
data class APIIETLesson(
    val id: Int,
    val type: APILessonType,
    val flows: List<APILessonFlow>,
    val teachers: List<APILessonTeacher>,
    val time: APILessonTime,
    val conference: APILessonConference?,
    val weekday: APILessonWeekDay
) {
    fun toLesson(week: Int): Pair<Lesson?, LessonConverterErrorMessage?> {
        return if(teachers.isEmpty()) Pair(null, LessonConverterErrorMessage.NO_TEACHER_FOR_LESSON)
        else if(flows.isEmpty()) Pair(null, LessonConverterErrorMessage.NO_DISCIPLINE_FOR_IET_LESSON)
        else Pair(Lesson(
            id = id,
            type = LessonType.getTypeFromName(type.name),
            discipline = flows[0].discipline.name,
            teacher = teachers[0].name,
            beginTime = time.beginTime,
            endTime = time.endTime,
            conferenceUrl = conference?.url,
            dayOfWeek = weekday.id,
            week = week
        ), null)
    }
}

@Serializable
data class APILessons(
    val lessons: List<APILesson>,
    val ietLessons: List<APIIETLesson>
) {
    fun toLessons(week: Int): Pair<List<Lesson>, List<LessonConverterErrorMessage>> {
        val databaseLessons = mutableListOf<Lesson>()
        val exceptions = mutableListOf<LessonConverterErrorMessage>()
        lessons.forEach { lesson ->
            val (databaseLesson, exception) = lesson.toLesson(week)
            if(databaseLesson != null) databaseLessons.add(databaseLesson)
            if(exception != null) exceptions.add(exception)
        }
        ietLessons.forEach { ietLesson ->
            val (databaseIetLesson, exception) = ietLesson.toLesson(week)
            if(databaseIetLesson != null) databaseLessons.add(databaseIetLesson)
            if(exception != null) exceptions.add(exception)
        }
        return Pair(databaseLessons, exceptions)
    }
}
