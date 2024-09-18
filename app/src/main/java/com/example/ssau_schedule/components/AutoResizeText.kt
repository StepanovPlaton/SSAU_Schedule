package com.example.ssau_schedule.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun AutoResizeText(
    text: String,
    fontSizeRange: FontSizeRange,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = LocalTextStyle.current,
) {
    val fontSizeValue = remember { mutableFloatStateOf(fontSizeRange.max.value) }
    val readyToDraw = remember { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        maxLines = maxLines,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        style = style,
        fontSize = fontSizeValue.floatValue.sp,
        onTextLayout = {
            if (it.didOverflowHeight && !readyToDraw.value) {
                val nextFontSizeValue = fontSizeValue.floatValue - fontSizeRange.step.value
                if (nextFontSizeValue <= fontSizeRange.min.value) {
                    fontSizeValue.floatValue = fontSizeRange.min.value
                    readyToDraw.value = true
                } else fontSizeValue.floatValue = nextFontSizeValue
            } else readyToDraw.value = true
        },
        modifier = modifier.drawWithContent { if (readyToDraw.value) drawContent() }
    )
}

data class FontSizeRange(
    val min: TextUnit,
    val max: TextUnit,
    val step: TextUnit = DEFAULT_TEXT_STEP,
) {
    init {
        require(min < max) { "min should be less than max, $this" }
        require(step.value > 0) { "step should be greater than 0, $this" }
    }

    companion object {
        private val DEFAULT_TEXT_STEP = 1.sp
    }
}