package com.example.ssau_schedule

import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

class Utils {
    companion object {

        @Composable
        fun keyboardState(): State<Boolean> {
            val keyboardOpen = remember { mutableStateOf(false) }
            val view = LocalView.current
            DisposableEffect(view) {
                val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
                    val rect = Rect()
                    view.getWindowVisibleDisplayFrame(rect)
                    val screenHeight = view.rootView.height
                    val keypadHeight = screenHeight - rect.bottom
                    keyboardOpen.value = keypadHeight > screenHeight * 0.15
                }
                view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)
                onDispose { view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener) }
            }
            return keyboardOpen
        }
    }

}