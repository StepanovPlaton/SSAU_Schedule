package com.example.ssau_schedule

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.ssau_schedule.api.AuthErrorMessage
import com.example.ssau_schedule.api.AuthorizationAPI
import com.example.ssau_schedule.api.GroupAPI
import com.example.ssau_schedule.api.GroupAPIErrorMessage
import com.example.ssau_schedule.api.Http
import com.example.ssau_schedule.api.UserAPI
import com.example.ssau_schedule.api.YearAPI
import com.example.ssau_schedule.api.YearAPIErrorMessage
import com.example.ssau_schedule.data.store.AuthStore
import com.example.ssau_schedule.data.store.Group
import com.example.ssau_schedule.data.store.GroupStore
import com.example.ssau_schedule.data.store.Year
import com.example.ssau_schedule.data.store.YearStore
import com.example.ssau_schedule.data.unsaved.User
import com.example.ssau_schedule.ui.theme.ApplicationColors
import com.example.ssau_schedule.ui.theme.SSAU_ScheduleTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import kotlin.math.min

class AuthActivity : ComponentActivity() {
    private val http = Http()
    private val authAPI = AuthorizationAPI(http)
    private val userAPI = UserAPI(http)
    private val groupAPI = GroupAPI(http)
    private val yearAPI = YearAPI(http)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            SSAU_ScheduleTheme {
                AuthPage()
            }
        }
    }

    @Composable
    fun AuthPage() {
        val authScope = rememberCoroutineScope()

        var user by remember { mutableStateOf<User?>(null) }
        var group by remember { mutableStateOf<Group?>(null) }
        var year by remember { mutableStateOf<Year?>(null) }


        var needAuth by remember { mutableStateOf(false) }
        var entered by remember { mutableStateOf(false) }

        val keyboardOpen by Utils.keyboardState()
        val snackbarHostState = remember { SnackbarHostState() }
        val logoHeight by animateFloatAsState(
            if (keyboardOpen && needAuth) 0f else min(
                LocalConfiguration.current.screenWidthDp,
                500
            ) / 101f * 48f,
            label = "alpha",
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        LaunchedEffect(user, group, year) {
            if(user != null && group != null && year != null) {
                delay(2500)
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }
        }

        LaunchedEffect(entered) {
            delay(3000)

            val token = AuthStore.getAuthToken(applicationContext)
            if(token == null) { needAuth = true; return@LaunchedEffect }

            val (userDetails) = userAPI.getUserDetails(token)
            if(userDetails == null) { needAuth = true; return@LaunchedEffect }
            else { user = userDetails }

            val (groups, groupsError) = groupAPI.getUserGroups(token)
            if(groups == null) {
                if(groupsError != null && groupsError !=
                    GroupAPIErrorMessage.USER_NOT_AUTHORIZED) {
                    val message = groupsError.getMessage(applicationContext)
                    if(message != null) snackbarHostState.showSnackbar(message)
                } else { needAuth = true; return@LaunchedEffect }
            } else {
                val currentGroup = GroupStore.getCurrentGroup(applicationContext)
                if(currentGroup != null && groups.contains(currentGroup)) group = currentGroup
                else {
                    GroupStore.setCurrentGroup(groups[0], applicationContext)
                    group = groups[0]
                }
            }

            val (years, yearsError) = yearAPI.getYears(token)
            if(years == null) {
                if(yearsError != null && yearsError !=
                    YearAPIErrorMessage.USER_NOT_AUTHORIZED) {
                    val message = yearsError.getMessage(applicationContext)
                    if(message != null) snackbarHostState.showSnackbar(message)
                } else { needAuth = true; return@LaunchedEffect }
            } else {
                val currentRawYear = years.find { y -> y.isCurrent }
                if(currentRawYear != null) {
                    year = currentRawYear.toYear()
                    YearStore.setCurrentYear(year!!, applicationContext, authScope)
                } else {
                    val message = YearAPIErrorMessage.FAILED_GET_YEARS
                        .getMessage(applicationContext)
                    if(message != null) snackbarHostState.showSnackbar(message)
                }
            }
        }

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
            Box(Modifier.background(MaterialTheme.colorScheme.primary)
                .fillMaxSize().padding(padding).imePadding(),
                contentAlignment = BiasAlignment(0f, -0.25f),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier
                            .widthIn(0.dp, 500.dp)
                            .height(logoHeight.dp)
                            .padding(20.dp, 0.dp)) {
                        Image(painterResource(R.drawable.ssau_logo_01),
                            contentDescription = stringResource(R.string.samara_university),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            contentScale = ContentScale.FillWidth,
                            alignment = Alignment.TopCenter)
                    }
                    Box(
                        Modifier
                            .padding(20.dp, 0.dp)
                            .widthIn(0.dp, 400.dp)) {
                        Column {
                            WelcomeMessage(user, group, year)
                            AuthForm(open = needAuth, authScope) {
                                needAuth = false
                                entered = true
                            }
                        }

                    }
                }
            }
        }

    }

    @Composable
    fun AuthForm(open: Boolean, scope: CoroutineScope, callback: () -> Unit) {
        var login by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var error by remember { mutableStateOf<AuthErrorMessage?>(null) }

        val height by animateDpAsState(if (open) 290.dp else 0.dp, label = "Auth form height",
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        Card(Modifier.fillMaxWidth().height(height).padding(0.dp, 10.dp).shadow(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
        ) {
            Column(Modifier.fillMaxWidth().padding(30.dp, 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.sign_in),
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displaySmall)
                OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                    value = login,
                    onValueChange = { login = it; error = null },
                    label = { Text(stringResource(R.string.login)) },
                    placeholder = { Text(stringResource(R.string.enter_your_login)) })
                Spacer(Modifier.height(2.dp))
                OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                    value = password,
                    onValueChange = { password = it; error = null },
                    label = { Text(stringResource(R.string.password)) },
                    placeholder = { Text(stringResource(R.string.enter_your_password)) })
                Spacer(Modifier.height(2.dp))
                Box(Modifier.fillMaxWidth().height(14.dp)) {
                    this@Column.AnimatedVisibility(
                        modifier = Modifier.align(Alignment.Center),
                        visible = error !== null
                    ) {
                        Text(error?.getMessage(applicationContext) ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall)
                    }
                }
                Spacer(Modifier.height(4.dp))
                FilledTonalButton(
                    onClick = {
                        if (login.length < 5) error = AuthErrorMessage.LOGIN_IS_TOO_SHORT
                        else if (password.length < 5) error = AuthErrorMessage.PASSWORD_IS_TOO_SHORT
                        else scope.launch {
                            val (token) = authAPI.signIn(login, password)
                            if(token != null) {
                                AuthStore.setAuthToken(token, applicationContext)
                                callback()
                            }
                            else error = AuthErrorMessage.INCORRECT_LOGIN_OR_PASSWORD
                        }
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) { Text(stringResource(R.string.sign_in)) }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Composable
    fun WelcomeMessage(user: User?, group: Group?, year: Year?) {
        val currentDate =  remember { SimpleDateFormat("d MMMM").format(Date()) }
        val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR); }
        Column(Modifier.fillMaxWidth().animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(user !== null && group != null && year != null) {
                Text("${stringResource(R.string.hello)} ${user.name}!",
                    color = ApplicationColors.White,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center)
                Text("${stringResource(R.string.schedule_for_group)} ${group.name}",
                    color = ApplicationColors.White,
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center)
                Text("$currentDate, ${year.getWeekOfDate(Date())} "+
                        "${stringResource(R.string.education_week)}, ${currentYear}-"+
                        "${currentYear+1} ${stringResource(R.string.education_year)}",
                    color = ApplicationColors.White,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center)
            }
        }
    }
}
