package com.example.stickerexplode

import androidx.compose.runtime.State
import com.example.stickerexplode.sensor.TiltData

actual fun createHolographicNode(tiltState: State<TiltData>): HolographicBaseNode {
    return HolographicFallbackNode(tiltState)
}
