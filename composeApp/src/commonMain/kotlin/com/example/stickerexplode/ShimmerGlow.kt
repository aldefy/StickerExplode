package com.example.stickerexplode

import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
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

private val RainbowColors = listOf(
    Color.Red,
    Color.Yellow,
    Color.Green,
    Color.Cyan,
    Color.Blue,
    Color.Magenta,
    Color.Red,
)

fun Modifier.shimmerGlow(tiltState: State<TiltData>): Modifier =
    this then ShimmerGlowElement(tiltState)

private data class ShimmerGlowElement(
    val tiltState: State<TiltData>,
) : ModifierNodeElement<ShimmerGlowNode>() {
    override fun create() = ShimmerGlowNode(tiltState)
    override fun update(node: ShimmerGlowNode) {
        node.tiltState = tiltState
    }
}

private class AngledRainbowBrush(
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
            colors = RainbowColors,
            colorStops = null,
            tileMode = TileMode.Mirror,
        )
    }
}

private class ShimmerGlowNode(
    var tiltState: State<TiltData>,
) : DrawModifierNode, Modifier.Node() {

    override fun ContentDrawScope.draw() {
        drawContent()

        val tilt = tiltState.value
        val roll = tilt.roll.coerceIn(-1f, 1f)
        val pitch = tilt.pitch.coerceIn(-1f, 1f)

        // Roll rotates the rainbow angle, pitch shifts the gradient position
        val angleDeg = 45f + roll * 60f
        val offsetX = roll * size.width * 0.5f
        val offsetY = pitch * size.height * 0.5f

        val rainbowBrush = AngledRainbowBrush(
            angleDeg = angleDeg,
            offset = Offset(offsetX, offsetY),
        )

        // SrcAtop: only draws where content already exists (respects sticker shape)
        // TileMode.Mirror gives a smooth repeating rainbow across the surface
        drawRect(
            brush = rainbowBrush,
            alpha = 0.13f + abs(roll) * 0.07f + abs(pitch) * 0.07f,
            blendMode = BlendMode.SrcAtop,
        )
    }
}
