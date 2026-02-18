package com.example.stickerexplode.sensor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.CoreMotion.CMMotionManager
import platform.Foundation.NSOperationQueue
import kotlin.math.PI

actual class TiltSensorProvider {
    private val motionManager = CMMotionManager()

    actual fun start(callback: (TiltData) -> Unit) {
        if (!motionManager.isDeviceMotionAvailable()) return
        motionManager.deviceMotionUpdateInterval = 1.0 / 30.0
        motionManager.startDeviceMotionUpdatesToQueue(NSOperationQueue.mainQueue) { motion, _ ->
            motion?.let {
                val pitch = (it.attitude.pitch / (PI / 2.0)).toFloat().coerceIn(-1f, 1f)
                val roll = (it.attitude.roll / (PI / 2.0)).toFloat().coerceIn(-1f, 1f)
                callback(TiltData(pitch, roll))
            }
        }
    }

    actual fun stop() {
        motionManager.stopDeviceMotionUpdates()
    }
}

@Composable
actual fun rememberTiltSensorProvider(): TiltSensorProvider {
    return remember { TiltSensorProvider() }
}
