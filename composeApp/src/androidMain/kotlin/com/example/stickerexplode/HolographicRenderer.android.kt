package com.example.stickerexplode

import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import com.example.stickerexplode.sensor.TiltData

actual fun createHolographicNode(tiltState: State<TiltData>): HolographicBaseNode {
    return if (Build.VERSION.SDK_INT >= 33) {
        AgslHolographicNode(tiltState)
    } else {
        HolographicFallbackNode(tiltState)
    }
}

// ── AGSL shader (API 33+) ───────────────────────────────────────────────────

private const val HOLOGRAPHIC_AGSL = """
uniform float2 resolution;
uniform float2 tilt; // x = roll, y = pitch

// Single smooth iridescent sweep — vivid but clean
vec3 iridescence(float t) {
    float phase = t * 6.2832;
    return vec3(
        0.7 + 0.3 * cos(phase + 0.0),
        0.7 + 0.3 * cos(phase + 2.094),
        0.75 + 0.25 * cos(phase + 4.189)
    );
}

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / resolution;
    float roll = tilt.x;
    float pitch = tilt.y;
    float tiltMag = clamp(length(tilt), 0.0, 1.0);

    // Layer 1: Smooth iridescent color sweep
    float angle = radians(45.0 + roll * 45.0);
    float2 dir = float2(cos(angle), sin(angle));
    float gradientT = dot(uv - 0.5, dir) + 0.5 + pitch * 0.3;
    vec3 iriColor = iridescence(gradientT);
    float iriAlpha = 0.12 + tiltMag * 0.06;

    // Layer 2: Specular glint — tracks tilt
    float2 specCenter = float2(0.5 + roll * 0.4, 0.5 + pitch * 0.4);
    float dist = distance(uv, specCenter);
    float specular = exp(-dist * dist * 8.0);
    float specAlpha = specular * 0.25;

    // Layer 3: Fresnel edge glow
    float edgeDist = distance(uv, float2(0.5, 0.5)) * 2.0;
    float fresnel = pow(clamp(edgeDist, 0.0, 1.0), 2.5);
    float fresnelAlpha = fresnel * (0.03 + tiltMag * 0.06);

    // Composite
    vec3 color = iriColor * iriAlpha
               + vec3(1.0) * specAlpha
               + vec3(1.0) * fresnelAlpha;
    float alpha = iriAlpha + specAlpha + fresnelAlpha;

    return half4(half3(color), half(clamp(alpha, 0.0, 0.45)));
}
"""

private class AgslHolographicNode(
    override var tiltState: State<TiltData>,
) : HolographicBaseNode() {

    private val runtimeShader = if (Build.VERSION.SDK_INT >= 33) {
        RuntimeShader(HOLOGRAPHIC_AGSL)
    } else {
        null
    }

    override fun ContentDrawScope.draw() {
        drawContent()

        val shader = runtimeShader ?: return
        if (Build.VERSION.SDK_INT < 33) return

        val tilt = tiltState.value
        val roll = tilt.roll.coerceIn(-1f, 1f)
        val pitch = tilt.pitch.coerceIn(-1f, 1f)

        shader.setFloatUniform("resolution", size.width, size.height)
        shader.setFloatUniform("tilt", roll, pitch)

        val shaderBrush = ShaderBrush(shader)

        drawRect(
            brush = shaderBrush,
            blendMode = BlendMode.SrcAtop,
        )
    }
}
