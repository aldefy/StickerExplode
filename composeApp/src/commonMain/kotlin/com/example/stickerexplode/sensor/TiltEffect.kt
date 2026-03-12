package com.example.stickerexplode.sensor

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.*

@Composable
fun rememberTiltState(enabled: Boolean = true): State<TiltData> {
    if (!enabled) {
        return remember { mutableStateOf(TiltData(0f, 0f)) }
    }

    val provider = rememberTiltSensorProvider()
    var rawPitch by remember { mutableStateOf(0f) }
    var rawRoll by remember { mutableStateOf(0f) }

    DisposableEffect(provider) {
        provider.start { data ->
            rawPitch = data.pitch
            rawRoll = data.roll
        }
        onDispose { provider.stop() }
    }

    val smoothPitch by animateFloatAsState(
        targetValue = rawPitch,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 200f),
    )
    val smoothRoll by animateFloatAsState(
        targetValue = rawRoll,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 200f),
    )

    return remember {
        derivedStateOf { TiltData(smoothPitch, smoothRoll) }
    }
}
