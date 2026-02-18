package com.example.stickerexplode.haptics

import androidx.compose.runtime.Composable

enum class HapticType {
    /** Light tick — sticker first grabbed / touch down */
    LightTap,
    /** Medium impact — sticker dropped after drag */
    MediumImpact,
    /** Heavy impact — long-press triggered */
    HeavyImpact,
    /** Selection click — context menu action selected */
    SelectionClick,
}

expect class HapticFeedbackProvider {
    fun perform(type: HapticType)
}

@Composable
expect fun rememberHapticFeedback(): HapticFeedbackProvider
