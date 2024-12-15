package com.example.ssau_schedule.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ssau_schedule.R
import com.example.ssau_schedule.api.Http
import com.example.ssau_schedule.api.LessonAPI
import com.example.ssau_schedule.data.base.Database
import com.example.ssau_schedule.data.store.StoreUtils
import com.example.ssau_schedule.widget.ScheduleWidget
import java.util.Date


class RequestLessonsWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager
    private val channelId = "ssau_schedule_1"
    private val notificationId = 1234

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        val mChannel = NotificationChannel(
            channelId,
            "SSAUScheduleNotificationChannel", NotificationManager.IMPORTANCE_HIGH
        )
        mChannel.enableLights(true)
        mChannel.enableVibration(true)
        notificationManager.createNotificationChannel(mChannel)

        val http = Http()
        val lessonAPI = LessonAPI(http)
        val database = Database.getInstance(context)

        val generalData = StoreUtils.getGeneralData(context)
        if (generalData == null) {
            pushErrorNotification()
            return Result.failure()
        }
        val week = generalData.year.getWeekOfDate(Date())
        val (apiLessons, apiErrors) = lessonAPI.getLessons(
            generalData.token,
            generalData.group,
            generalData.year,
            week,
        )
        if (apiErrors != null || apiLessons == null) {
            pushErrorNotification()
            return Result.failure()
        }

        val (lessons, convertErrors) = apiLessons.toLessons(week)
        if (convertErrors.isNotEmpty()) {
            pushErrorNotification()
            return Result.failure()
        }
        database.lessonDao().insert(*lessons.toTypedArray())
        ScheduleWidget().updateAll(context)
        return Result.success()
    }

    private fun pushErrorNotification() {
        notificationManager.notify(
            notificationId,
            NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(context.resources.getString(R.string.failed_get_schedule))
                .setContentText(context.resources.getString(R.string.log_into_app_to_update))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
        )
    }
}