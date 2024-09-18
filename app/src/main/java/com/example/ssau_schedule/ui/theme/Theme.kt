package com.example.ssau_schedule.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ApplicationColors.Primary01,
    secondary = ApplicationColors.White,
    tertiary = ApplicationColors.Gray04,
    background = ApplicationColors.Gray01,
    surface = ApplicationColors.Gray02,
    error = ApplicationColors.Red01,
    errorContainer = ApplicationColors.Red02
)

private val LightColorScheme = lightColorScheme(
    primary = ApplicationColors.Primary01,
    secondary = ApplicationColors.Gray01,
    tertiary = ApplicationColors.Gray03,
    background = ApplicationColors.White,
    surface = ApplicationColors.Primary06,
    error = ApplicationColors.Red01,
    errorContainer = ApplicationColors.Red02,
)

@Composable
fun SSAU_ScheduleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) = MaterialTheme(
    colorScheme = if(darkTheme) DarkColorScheme else LightColorScheme,
    typography = Typography,
    content = content
)
