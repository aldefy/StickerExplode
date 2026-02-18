package com.example.stickerexplode.sensor

import androidx.compose.runtime.Composable

data class TiltData(val pitch: Float = 0f, val roll: Float = 0f)

expect class TiltSensorProvider {
    fun start(callback: (TiltData) -> Unit)
    fun stop()
}

@Composable
expect fun rememberTiltSensorProvider(): TiltSensorProvider
