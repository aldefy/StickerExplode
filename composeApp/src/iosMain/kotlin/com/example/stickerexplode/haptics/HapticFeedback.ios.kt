package com.example.stickerexplode.haptics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.UIKit.UISelectionFeedbackGenerator

actual class HapticFeedbackProvider {
    private val lightGenerator = UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleLight)
    private val mediumGenerator = UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium)
    private val heavyGenerator = UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy)
    private val selectionGenerator = UISelectionFeedbackGenerator()

    actual fun perform(type: HapticType) {
        when (type) {
            HapticType.LightTap -> lightGenerator.impactOccurred()
            HapticType.MediumImpact -> mediumGenerator.impactOccurred()
            HapticType.HeavyImpact -> heavyGenerator.impactOccurred()
            HapticType.SelectionClick -> selectionGenerator.selectionChanged()
        }
    }
}

@Composable
actual fun rememberHapticFeedback(): HapticFeedbackProvider {
    return remember { HapticFeedbackProvider() }
}
