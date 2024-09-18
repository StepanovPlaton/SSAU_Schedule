package com.example.ssau_schedule.work

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.ssau_schedule.api.Http
import com.example.ssau_schedule.api.LessonAPI
import com.example.ssau_schedule.data.base.Database
import com.example.ssau_schedule.data.store.StoreUtils

//class RequestLessonsWorker(
//    private val context: Context,
//    private val workerParams: WorkerParameters
//): CoroutineWorker(context, workerParams) {
//    private val notificationManager =
//        context.getSystemService(Context.NOTIFICATION_SERVICE) as
//                NotificationManager
//
//    override suspend fun doWork(): Result {
//        val http = Http()
//        val lessonAPI = LessonAPI(http)
//        val database = Database.getInstance(context)
//
//        val generalData = StoreUtils.getGeneralData(context) ?: return Result.failure()
//        val week = inputData.getInt("week", -1)
//        if(week == -1) return Result.failure()
//
//
//        val (apiLessons, apiErrors) = lessonAPI.getLessons(
//            generalData.token,
//            generalData.group,
//            generalData.year,
//            week,
//        )
//        if(apiErrors != null || apiLessons == null) return Result.failure()
//
//        val (lessons, convertErrors) = apiLessons.toLessons(week)
//        if(convertErrors.isNotEmpty()) {
//            var builder = NotificationCompat.Builder(context, "1")
//                .setContentTitle("Title")
//                .setContentText("Content")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//        }
//        database.lessonDao().insert(*lessons.to)
//    }
//}