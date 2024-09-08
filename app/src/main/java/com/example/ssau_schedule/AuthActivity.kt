package com.example.ssau_schedule

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.ssau_schedule.ui.theme.SSAU_ScheduleTheme
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.ssau_schedule.api.AuthErrorMessage
import com.example.ssau_schedule.api.AuthorizationAPI
import com.example.ssau_schedule.api.Http
import kotlin.math.min

class AuthActivity : ComponentActivity() {
    private val http = Http()
    private var auth: AuthorizationAPI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = AuthorizationAPI.getInstance(http, applicationContext)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            SSAU_ScheduleTheme {
                AuthPage()
            }
        }
    }

    @Composable
    fun AuthPage() {
        var loginOpen by remember { mutableStateOf(false) }
        val keyboardOpen by Utils.keyboardState()
        val logoHeight by animateFloatAsState(
            if (keyboardOpen) 0f else min(LocalConfiguration.current.screenWidthDp, 500) / 101f * 48f,
            label = "alpha",
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ))


        LaunchedEffect(false) {
            delay(1000)
            loginOpen = true
        }

        Box(Modifier.background(MaterialTheme.colorScheme.primary)
                .fillMaxSize().statusBarsPadding().navigationBarsPadding().imePadding(),
            contentAlignment = BiasAlignment(0f, -0.25f),
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.widthIn(0.dp, 500.dp)
                    .height(logoHeight.dp).padding(20.dp, 0.dp)
                ) {
                    Image(painterResource(R.drawable.ssau_logo_01),
                        contentDescription = stringResource(R.string.samara_university),
                        modifier = Modifier.fillMaxSize().padding(10.dp),
                        contentScale = ContentScale.FillWidth,
                        alignment = Alignment.TopCenter)
                }
                Box(Modifier.padding(20.dp, 0.dp).widthIn(0.dp, 400.dp)) {
                    Card(Modifier.fillMaxWidth().animateContentSize(animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = 50f
                        ))
                        .height(if(loginOpen) 280.dp else 0.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.background,
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                    ) {
                        AuthForm()
                    }
                }
            }
        }
    }

    @Composable
    fun AuthForm() {
        val authScope = rememberCoroutineScope()
        var login by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var error by remember { mutableStateOf<AuthErrorMessage?>(null) }

        Column(Modifier.fillMaxWidth().padding(30.dp, 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.sign_in),
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displaySmall
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = login,
                onValueChange = { login = it; error = null },
                label = { Text(stringResource(R.string.login)) },
                placeholder =
                { Text(stringResource(R.string.enter_your_login)) },
            )
            Spacer(Modifier.height(2.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = { password = it; error = null },
                label = { Text(stringResource(R.string.password)) },
                placeholder =
                { Text(stringResource(R.string.enter_your_password)) },
            )
            Spacer(Modifier.height(2.dp))
            Box(Modifier.fillMaxWidth().height(14.dp)) {
                this@Column.AnimatedVisibility(
                    modifier = Modifier.align(Alignment.Center),
                    visible = error !== null,
                    enter = fadeIn(animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessVeryLow
                    )),
                    exit = fadeOut(animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessVeryLow
                    ))
                ) {
                    Text(error?.getMessage(applicationContext) ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(Modifier.height(4.dp))
            FilledTonalButton(onClick = {
                if(login.length < 5) error = AuthErrorMessage.LOGIN_IS_TOO_SHORT
                else if(password.length < 5) error = AuthErrorMessage.PASSWORD_IS_TOO_SHORT
                else {
                    auth?.signIn(login, password, authScope,
                        { startActivity(Intent(applicationContext, AuthActivity::class.java)) },
                        { _, _ ->
                            error = AuthErrorMessage.INCORRECT_LOGIN_OR_PASSWORD
                        }
                    )
                }
            },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(stringResource(R.string.sign_in))
            }
        }
    }
}
