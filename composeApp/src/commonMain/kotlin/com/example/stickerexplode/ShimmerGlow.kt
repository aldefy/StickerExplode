package com.example.stickerexplode

import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import com.example.stickerexplode.sensor.TiltData
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// ── Public API ──────────────────────────────────────────────────────────────

fun Modifier.holographicShine(tiltState: State<TiltData>): Modifier =
    this then HolographicShineElement(tiltState)

// ── expect/actual for platform-specific renderer ────────────────────────────

expect fun createHolographicNode(tiltState: State<TiltData>): HolographicBaseNode

// ── Common 3-layer fallback renderer ────────────────────────────────────────

private val IridescentColors = listOf(
    Color(0xFFE0D4FF), // soft lavender
    Color(0xFFD0EAFF), // pale sky
    Color(0xFFD4F0E0), // soft mint
    Color(0xFFFFECD0), // warm cream
    Color(0xFFFFD8D8), // blush pink
    Color(0xFFE0D4FF), // soft lavender (wrap)
)

/** Layer 1: Iridescent thin-film gradient — angle rotates with roll, offset shifts with pitch */
private class IridescentBrush(
    private val angleDeg: Float,
    private val offset: Offset,
) : ShaderBrush() {
    override fun createShader(size: Size): Shader {
        val angleRad = Math.toRadians(angleDeg.toDouble())
        val diagonal = sqrt(size.width.pow(2) + size.height.pow(2))
        val cx = size.width / 2f
        val cy = size.height / 2f
        val halfDiag = diagonal / 2f

        val startX = cx - halfDiag * cos(angleRad).toFloat() + offset.x
        val startY = cy - halfDiag * sin(angleRad).toFloat() + offset.y
        val endX = cx + halfDiag * cos(angleRad).toFloat() + offset.x
        val endY = cy + halfDiag * sin(angleRad).toFloat() + offset.y

        return LinearGradientShader(
            from = Offset(startX, startY),
            to = Offset(endX, endY),
            colors = IridescentColors,
            colorStops = null,
            tileMode = TileMode.Clamp,
        )
    }
}

/** Layer 2: Specular hotspot — white radial that tracks tilt position */
private class SpecularBrush(
    private val centerX: Float,
    private val centerY: Float,
) : ShaderBrush() {
    override fun createShader(size: Size): Shader {
        val cx = size.width * centerX
        val cy = size.height * centerY
        val radius = maxOf(size.width, size.height) * 0.8f
        return RadialGradientShader(
            center = Offset(cx, cy),
            radius = radius,
            colors = listOf(Color.White, Color.Transparent),
            colorStops = listOf(0f, 1f),
            tileMode = TileMode.Clamp,
        )
    }
}

/** Layer 3: Fresnel edge glow — transparent center → white edges */
private class FresnelBrush(
    private val intensity: Float,
) : ShaderBrush() {
    override fun createShader(size: Size): Shader {
        val center = size.center
        val radius = maxOf(size.width, size.height) * 0.5f
        return RadialGradientShader(
            center = center,
            radius = radius,
            colors = listOf(Color.Transparent, Color.White.copy(alpha = intensity)),
            colorStops = listOf(0.3f, 1f),
            tileMode = TileMode.Clamp,
        )
    }
}

// ── Base node class ─────────────────────────────────────────────────────────

abstract class HolographicBaseNode : DrawModifierNode, Modifier.Node() {
    abstract var tiltState: State<TiltData>
}

// ── Common fallback node (used by iOS and Android < 33) ─────────────────────

class HolographicFallbackNode(
    override var tiltState: State<TiltData>,
) : HolographicBaseNode() {

    override fun ContentDrawScope.draw() {
        drawContent()

        val tilt = tiltState.value
        val roll = tilt.roll.coerceIn(-1f, 1f)
        val pitch = tilt.pitch.coerceIn(-1f, 1f)
        val tiltMagnitude = sqrt(roll * roll + pitch * pitch).coerceIn(0f, 1f)

        // Layer 1: Iridescent gradient — single smooth sweep
        val angleDeg = 45f + roll * 45f
        val offsetX = roll * size.width * 0.4f
        val offsetY = pitch * size.height * 0.4f
        drawRect(
            brush = IridescentBrush(angleDeg, Offset(offsetX, offsetY)),
            alpha = 0.12f + tiltMagnitude * 0.06f,
            blendMode = BlendMode.SrcAtop,
        )

        // Layer 2: Specular glint
        val specCx = 0.5f + roll * 0.4f
        val specCy = 0.5f + pitch * 0.4f
        drawRect(
            brush = SpecularBrush(specCx, specCy),
            alpha = 0.25f,
            blendMode = BlendMode.Screen,
        )

        // Layer 3: Fresnel edge glow
        val fresnelAlpha = 0.03f + tiltMagnitude * 0.06f
        drawRect(
            brush = FresnelBrush(intensity = fresnelAlpha * 2.5f),
            alpha = fresnelAlpha,
            blendMode = BlendMode.SrcAtop,
        )
    }
}

// ── ModifierNodeElement ─────────────────────────────────────────────────────

private data class HolographicShineElement(
    val tiltState: State<TiltData>,
) : ModifierNodeElement<HolographicBaseNode>() {
    override fun create(): HolographicBaseNode = createHolographicNode(tiltState)
    override fun update(node: HolographicBaseNode) {
        node.tiltState = tiltState
    }
}
