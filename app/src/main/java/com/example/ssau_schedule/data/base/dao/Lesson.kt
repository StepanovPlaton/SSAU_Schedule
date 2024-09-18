package com.example.ssau_schedule.data.base.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ssau_schedule.data.base.entity.lesson.Lesson

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons")
    suspend fun getAll(): List<Lesson>


    @Query("SELECT * FROM lessons WHERE id IN (:ids)")
    suspend fun getById(ids: IntArray): List<Lesson>

    @Query("SELECT * FROM lessons WHERE id = (:id) LIMIT 1")
    suspend fun getById(id: Int): Lesson


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lesson: Lesson)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg lessons: Lesson)


    @Delete
    suspend fun delete(lesson: Lesson)
}