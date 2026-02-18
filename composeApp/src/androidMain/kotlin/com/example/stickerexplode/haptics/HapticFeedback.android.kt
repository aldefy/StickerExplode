package com.example.stickerexplode.haptics

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

actual class HapticFeedbackProvider(private val view: View) {
    actual fun perform(type: HapticType) {
        val constant = when (type) {
            HapticType.LightTap -> HapticFeedbackConstants.CLOCK_TICK
            HapticType.MediumImpact -> HapticFeedbackConstants.CONFIRM
            HapticType.HeavyImpact -> HapticFeedbackConstants.LONG_PRESS
            HapticType.SelectionClick -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    HapticFeedbackConstants.GESTURE_START
                } else {
                    HapticFeedbackConstants.CONTEXT_CLICK
                }
            }
        }
        view.performHapticFeedback(constant)
    }
}

@Composable
actual fun rememberHapticFeedback(): HapticFeedbackProvider {
    val view = LocalView.current
    return remember { HapticFeedbackProvider(view) }
}
