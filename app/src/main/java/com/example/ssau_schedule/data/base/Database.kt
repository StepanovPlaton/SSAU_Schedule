package com.example.ssau_schedule.data.base

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.ssau_schedule.data.base.dao.LessonDao
import com.example.ssau_schedule.data.base.entity.lesson.Lesson
import com.example.ssau_schedule.data.base.entity.lesson.LessonType

class Converters {
    @TypeConverter fun toLessonType(value: String) = LessonType.getTypeFromName(value)
    @TypeConverter fun fromLessonType(value: LessonType) = value.displayName
}

@androidx.room.Database(
    entities = [Lesson::class],
    version = 1,
    autoMigrations = [])
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun lessonDao(): LessonDao

    companion object {
        @Volatile
        private var database: Database? = null
        fun getInstance(context: Context): Database =
            database
                ?: synchronized(this) {
                    database
                        ?: Room.databaseBuilder(
                            context,
                            Database::class.java, "database"
                        ).build()
                }
    }
}
