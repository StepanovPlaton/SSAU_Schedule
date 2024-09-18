package com.example.ssau_schedule

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.ssau_schedule.api.Http
import com.example.ssau_schedule.api.LessonAPI
import com.example.ssau_schedule.api.LessonAPIErrorMessage
import com.example.ssau_schedule.components.LessonCards
import com.example.ssau_schedule.data.base.Database
import com.example.ssau_schedule.data.base.entity.lesson.Lesson
import com.example.ssau_schedule.data.store.StoreUtils
import com.example.ssau_schedule.ui.theme.SSAU_ScheduleTheme
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Date

class MainActivity : ComponentActivity() {
    private val http = Http()
    private val lessonAPI = LessonAPI(http)
    private lateinit var database: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            SSAU_ScheduleTheme {
                MainPage()
            }
        }
    }

    @Composable
    fun MainPage() {
        database = remember { Database.getInstance(applicationContext) }
        val snackbarHostState = remember { SnackbarHostState() }
        val lessons = remember { mutableStateOf<List<Lesson>>(listOf()) }

        val currentDate = remember { mutableStateOf(Date()) }
        val currentDayOfWeek = remember {
            mutableIntStateOf(Utils.Date.getDateOfWeek(currentDate.value))
        }
        val pagerState = rememberPagerState(
            initialPage = currentDayOfWeek.intValue-1, pageCount = {Int.MAX_VALUE})

        LaunchedEffect(false) {
            lessons.value = database.lessonDao().getAll()
        }

//        LaunchedEffect(false) {
//            val generalData = StoreUtils.getGeneralData(applicationContext)
//            if(generalData == null)
//                startActivity(Intent(applicationContext, AuthActivity::class.java))
//            else {
//                val week = generalData.year.getWeekOfDate(Date())
//                val (apiLessons, apiError) = lessonAPI.getLessons(
//                    generalData.token, generalData.group, generalData.year, week)
//                if(apiLessons != null && apiError == null) {
//                    val (databaseLessons, converterErrors) = apiLessons.toLessons(week)
//                    Log.i("Lessons", Json.encodeToString(apiLessons))
//                    database.lessonDao().insert(*databaseLessons.toTypedArray())
//                    converterErrors.forEach { error ->
//                        val message = error.getMessage(applicationContext)
//                        if(message != null) snackbarHostState.showSnackbar(message)
//                    }
//                    lessons.value = databaseLessons
//                } else {
//                    if(apiError == LessonAPIErrorMessage.USER_NOT_AUTHORIZED) {
//                        startActivity(Intent(applicationContext, AuthActivity::class.java))
//                    } else {
//                        val message = apiError?.getMessage(applicationContext)
//                        if(message != null) snackbarHostState.showSnackbar(message)
//                    }
//                }
//            }
//        }

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) {
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
                Column {
                    Box(Modifier.fillMaxWidth().height(60.dp)) {
                        Row(Modifier.fillMaxSize().padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Box(Modifier.height(40.dp).width(40.dp)
                                .shadow(elevation = 6.dp, shape = RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.surface),
                            ) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = "Forward icon",
                                    Modifier.fillMaxSize(),
                                    tint = MaterialTheme.colorScheme.primary)
                            }
                            Box(Modifier.height(40.dp)
                                .shadow(elevation = 6.dp, shape = RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.surface),
                            ) {
                                Row(Modifier.fillMaxHeight().padding(10.dp, 0.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.DateRange,
                                        contentDescription = "Date icon",
                                        Modifier.height(40.dp).padding(0.dp, 0.dp, 10.dp, 0.dp),
                                        tint = MaterialTheme.colorScheme.primary)
                                    Text(Utils.Date.format(currentDate.value),
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                            Box(Modifier.height(40.dp).width(40.dp)
                                .shadow(elevation = 6.dp, shape = RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.surface),
                            ) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Forward icon",
                                    Modifier.fillMaxSize(),
                                    tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                    HorizontalDivider(Modifier.padding(20.dp, 0.dp))
                    HorizontalPager(state = pagerState) { _ ->
                        LessonCards(lessons.value)
                    }
                }
            }
        }

    }
}