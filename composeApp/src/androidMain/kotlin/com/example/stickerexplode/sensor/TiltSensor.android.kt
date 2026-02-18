package com.example.stickerexplode.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlin.math.PI

actual class TiltSensorProvider(private val context: Context) {
    private var sensorManager: SensorManager? = null
    private var listener: SensorEventListener? = null

    actual fun start(callback: (TiltData) -> Unit) {
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager = sm
        val sensor = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) ?: return

        val rotationMatrix = FloatArray(9)
        val orientation = FloatArray(3)

        listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                SensorManager.getOrientation(rotationMatrix, orientation)
                val pitch = (orientation[1] / (PI / 2.0)).toFloat().coerceIn(-1f, 1f)
                val roll = (orientation[2] / (PI / 2.0)).toFloat().coerceIn(-1f, 1f)
                callback(TiltData(pitch, roll))
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
    }

    actual fun stop() {
        listener?.let { sensorManager?.unregisterListener(it) }
        listener = null
        sensorManager = null
    }
}

@Composable
actual fun rememberTiltSensorProvider(): TiltSensorProvider {
    val context = LocalContext.current
    return remember { TiltSensorProvider(context) }
}
