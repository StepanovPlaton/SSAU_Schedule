package com.example.ssau_schedule

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.updateAll
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.ssau_schedule.api.Http
import com.example.ssau_schedule.api.LessonAPI
import com.example.ssau_schedule.api.LessonAPIErrorMessage
import com.example.ssau_schedule.components.EmptyDay
import com.example.ssau_schedule.components.LessonCards
import com.example.ssau_schedule.data.base.Database
import com.example.ssau_schedule.data.base.entity.lesson.Lesson
import com.example.ssau_schedule.data.store.GeneralData
import com.example.ssau_schedule.data.store.StoreUtils
import com.example.ssau_schedule.data.store.Year
import com.example.ssau_schedule.ui.theme.SSAU_ScheduleTheme
import com.example.ssau_schedule.widget.ScheduleWidget
import com.example.ssau_schedule.work.RequestLessonsWorker
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val http = Http()
    private val lessonAPI = LessonAPI(http)
    private lateinit var database: Database

    private lateinit var workManager: WorkManager
    private val workName = "SSAUSchedule"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SSAU_ScheduleTheme {
                MainPage()
            }
        }
    }

    @Composable
    fun MainPage() {
        database = remember { Database.getInstance(applicationContext) }
        workManager = remember { WorkManager.getInstance(applicationContext) }
        val notificationState = remember { SnackbarHostState() }
        val lessons = remember { mutableStateOf<List<Lesson>>(listOf()) }
        val animationScope = rememberCoroutineScope()
        val pagerState = rememberPagerState(
            initialPage = Utils.Date.getDayOfWeek(Date()), pageCount = { Int.MAX_VALUE })
        val studyYear = remember { mutableStateOf<Year?>(null) }
        val loadedWeeks = remember { mutableStateOf<List<Int>>(listOf()) }
        val workStarted = remember { mutableStateOf(false) }

        suspend fun getLessons(generalData: GeneralData, week: Int) {
            val (apiLessons, apiError) = lessonAPI.getLessons(
                generalData.token, generalData.group, generalData.year, week
            )
            if (apiLessons != null && apiError == null) {
                val (databaseLessons, converterErrors) = apiLessons.toLessons(week)
                database.lessonDao().insert(*databaseLessons.toTypedArray())
                converterErrors.forEach { error ->
                    val message = error.getMessage(applicationContext)
                    if (message != null) notificationState.showSnackbar(message)
                }
                lessons.value = lessons.value.plus(databaseLessons)
                loadedWeeks.value = loadedWeeks.value.plus(week)
                ScheduleWidget().updateAll(applicationContext)
            } else {
                if (apiError == LessonAPIErrorMessage.USER_NOT_AUTHORIZED) {
                    startActivity(Intent(applicationContext, AuthActivity::class.java))
                } else {
                    val message = apiError?.getMessage(applicationContext)
                    if (message != null) notificationState.showSnackbar(message)
                }
            }
        }

        LaunchedEffect(pagerState.currentPage) {
            val generalData = StoreUtils.getGeneralData(applicationContext)
            if (generalData == null)
                startActivity(Intent(applicationContext, AuthActivity::class.java))
            else {
                if (!workStarted.value) {
                    val workRequest = PeriodicWorkRequestBuilder<RequestLessonsWorker>(
                        repeatInterval = 3,
                        TimeUnit.HOURS
                    ).setInitialDelay(1, TimeUnit.HOURS).build()
                    workManager.enqueueUniquePeriodicWork(
                        workName,
                        ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                        workRequest
                    )
                    workStarted.value = true
                }

                studyYear.value = generalData.year
                val day = Utils.Date.addDays(
                    Date(),
                    pagerState.currentPage - Utils.Date.getDayOfWeek(Date())
                )
                var week = generalData.year.getWeekOfDate(day)
                if (!loadedWeeks.value.contains(week)) getLessons(generalData, week)
                if (Utils.Date.getDayOfWeek(day) == 6) week++
                if (!loadedWeeks.value.contains(week)) getLessons(generalData, week)
            }
        }

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = notificationState) {
                    Snackbar(
                        snackbarData = it,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        ) { padding ->
            Box(
                Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize()
                    .padding(padding)
                    .imePadding(),
            ) {
                Column(Modifier.fillMaxHeight()) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(60.dp)) {
                        Row(
                            Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                Modifier
                                    .height(40.dp)
                                    .width(40.dp)
                                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(50))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clickable {
                                        animationScope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                        }
                                    },
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = "Forward icon",
                                    Modifier.fillMaxSize(),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Box(
                                Modifier
                                    .height(40.dp)
                                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(50))
                                    .background(MaterialTheme.colorScheme.surface),
                            ) {
                                Row(
                                    Modifier
                                        .fillMaxHeight()
                                        .padding(10.dp, 0.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = "Date icon",
                                        Modifier
                                            .height(40.dp)
                                            .padding(0.dp, 0.dp, 10.dp, 0.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        Utils.Date.format(
                                            Utils.Date.addDays(
                                                Date(),
                                                pagerState.currentPage - Utils.Date.getDayOfWeek(
                                                    Date()
                                                )
                                            )
                                        ),
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                            Box(
                                Modifier
                                    .height(40.dp)
                                    .width(40.dp)
                                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(50))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clickable {
                                        animationScope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                        }
                                    },
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Forward icon",
                                    Modifier.fillMaxSize(),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    HorizontalDivider(Modifier.padding(20.dp, 0.dp))
                    HorizontalPager(state = pagerState) { page ->
                        val todayLessons = lessons.value.filter { lesson ->
                            lesson.dayOfWeek - 1 == Utils.Date.getDayOfWeek(
                                Utils.Date.addDays(Date(), page - Utils.Date.getDayOfWeek(Date()))
                            ) &&
                                    lesson.week - 1 == Utils.Date.getWeekOfStudyYear(
                                Utils.Date.addDays(Date(), page - Utils.Date.getDayOfWeek(Date()))
                            )
                        }.sortedBy { lesson -> lesson.beginTime }
                        if (todayLessons.isEmpty())
                            EmptyDay(Modifier)
                        else
                            LessonCards(todayLessons)
                    }
                }
            }
        }
    }
}