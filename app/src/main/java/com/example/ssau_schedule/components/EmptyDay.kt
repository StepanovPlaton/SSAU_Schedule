package com.example.ssau_schedule.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ssau_schedule.R
import com.example.ssau_schedule.ui.theme.LessonColors

@Composable
fun EmptyDay(modifier: Modifier) {
    Box(
        Modifier
            .fillMaxHeight()
            .fillMaxWidth()) {
        Row(
            modifier
                .fillMaxWidth()
                .padding(14.dp, 8.dp)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp))
                .background(
                    if (isSystemInDarkTheme()) LessonColors.Background.Dark.Unknown
                    else LessonColors.Background.Light.Unknown
                ),
        ) {
            AutoResizeText(
                stringResource(R.string.no_classes_today),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                fontSizeRange = FontSizeRange(10.sp, 24.sp),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}