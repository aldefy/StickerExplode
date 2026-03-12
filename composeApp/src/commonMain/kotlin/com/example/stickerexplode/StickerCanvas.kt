package com.example.stickerexplode

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.stickerexplode.model.StickerItem
import com.example.stickerexplode.model.StickerType
import com.example.stickerexplode.haptics.HapticFeedbackProvider
import com.example.stickerexplode.haptics.HapticType
import com.example.stickerexplode.haptics.rememberHapticFeedback
import com.example.stickerexplode.sensor.TiltData
import com.example.stickerexplode.sensor.rememberTiltState
import com.example.stickerexplode.viewmodel.CanvasViewModel
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun StickerCanvas(
    viewModel: CanvasViewModel,
    onNavigateToHistory: () -> Unit,
) {
    val sensorEnabled by viewModel.sensorEnabled.collectAsState()
    val tiltState = rememberTiltState(enabled = sensorEnabled)
    val haptics = rememberHapticFeedback()
    val stickers by viewModel.stickers.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F7)),
        ) {
            val maxW = constraints.maxWidth.toFloat()
            val maxH = constraints.maxHeight.toFloat()

            stickers.forEach { sticker ->
                DraggableSticker(
                    sticker = sticker,
                    maxWidth = maxW,
                    maxHeight = maxH,
                    tiltState = tiltState,
                    haptics = haptics,
                    zIndex = sticker.zIndex,
                    onTapped = {
                        viewModel.bringToFront(sticker.id)
                    },
                    onTransformChanged = { offsetX, offsetY, scale, rotation ->
                        viewModel.updateStickerTransform(
                            id = sticker.id,
                            offsetX = offsetX,
                            offsetY = offsetY,
                            scale = scale,
                            rotation = rotation,
                        )
                    },
                )
            }
        }

        // History FAB — bottom-left
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomStart,
        ) {
            FloatingActionButton(
                onClick = {
                    haptics.perform(HapticType.SelectionClick)
                    onNavigateToHistory()
                },
                modifier = Modifier.padding(24.dp),
                shape = CircleShape,
                containerColor = Color.White,
                contentColor = Color(0xFF5B5FE6),
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 12.dp,
                ),
            ) {
                Icon(
                    imageVector = Icons.Filled.History,
                    contentDescription = "Sticker history",
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        // Sensor toggle FAB — bottom-right, above the + FAB
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd,
        ) {
            FloatingActionButton(
                onClick = {
                    haptics.perform(HapticType.SelectionClick)
                    viewModel.toggleSensor()
                },
                modifier = Modifier.padding(end = 24.dp, bottom = 96.dp),
                shape = CircleShape,
                containerColor = Color.White,
                contentColor = if (sensorEnabled) Color(0xFF5B5FE6) else Color(0xFFAAAAAA),
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 12.dp,
                ),
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = if (sensorEnabled) "Disable tilt sensor" else "Enable tilt sensor",
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        // Tilt angle float — bottom center, glass design
        AnimatedVisibility(
            visible = sensorEnabled,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
        ) {
            TiltAngleFloat(tiltState)
        }

        StickerTray(
            haptics = haptics,
            onStickerSelected = { type ->
                viewModel.addSticker(type)
            },
        )
    }
}

@Composable
private fun DraggableSticker(
    sticker: StickerItem,
    maxWidth: Float,
    maxHeight: Float,
    tiltState: State<TiltData>,
    haptics: HapticFeedbackProvider,
    zIndex: Float,
    onTapped: () -> Unit,
    onTransformChanged: (offsetX: Float, offsetY: Float, scale: Float, rotation: Float) -> Unit,
) {
    // Initialize from persisted state if available, otherwise from fraction
    var offset by remember {
        mutableStateOf(
            if (!sticker.offsetX.isNaN() && !sticker.offsetY.isNaN()) {
                Offset(sticker.offsetX, sticker.offsetY)
            } else {
                Offset(
                    sticker.initialFractionX * maxWidth,
                    sticker.initialFractionY * maxHeight,
                )
            }
        )
    }
    var pinchScale by remember { mutableFloatStateOf(sticker.pinchScale) }
    var rotation by remember { mutableFloatStateOf(sticker.rotation) }
    var isZoomedIn by remember { mutableStateOf(false) }
    val doubleTapScale by animateFloatAsState(
        targetValue = if (isZoomedIn) 2f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
    )
    val combinedScale = pinchScale * doubleTapScale
    var isDragging by remember { mutableStateOf(false) }
    var hasBeenLifted by remember { mutableStateOf(false) }

    // Peel-off: sticker lifts when first grabbed
    val peelScale by animateFloatAsState(
        targetValue = if (isDragging) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 300f),
    )
    val peelRotationX by animateFloatAsState(
        targetValue = if (isDragging) -6f else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 250f),
    )
    val peelTranslateY by animateFloatAsState(
        targetValue = if (isDragging) -8f else 0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
    )
    // Dynamic shadow: 0f = resting, 1f = fully lifted
    val liftFraction by animateFloatAsState(
        targetValue = if (isDragging) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 200f),
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .zIndex(zIndex)
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .graphicsLayer {
                scaleX = combinedScale * peelScale
                scaleY = combinedScale * peelScale
                rotationZ = rotation
                rotationX = peelRotationX
                translationY = peelTranslateY
                cameraDistance = 12f * density
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, gestureRotation ->
                    if (!isDragging) {
                        isDragging = true
                        hasBeenLifted = true
                        haptics.perform(HapticType.LightTap)
                    }
                    pinchScale = (pinchScale * zoom).coerceIn(0.5f, 3f)
                    rotation += gestureRotation
                    offset += pan
                }
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.changes.all { !it.pressed } && isDragging) {
                            isDragging = false
                            haptics.perform(HapticType.MediumImpact)
                            onTransformChanged(offset.x, offset.y, pinchScale, rotation)
                        }
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        haptics.perform(HapticType.SelectionClick)
                        onTapped()
                    },
                    onDoubleTap = {
                        haptics.perform(HapticType.MediumImpact)
                        isZoomedIn = !isZoomedIn
                    },
                )
            },
    ) {
        StickerVisual(type = sticker.type, liftFraction = liftFraction, tiltState = tiltState)
    }
}

/**
 * Small frosted-glass float showing the phone's roll angle (left/right tilt).
 * Roll is normalized [-1, 1] — we convert to degrees [-90°, 90°].
 */
@Composable
private fun TiltAngleFloat(tiltState: State<TiltData>) {
    val tilt by tiltState
    val degrees = (tilt.roll * 90f).roundToInt()
    val direction = when {
        degrees < -2 -> "L"
        degrees > 2 -> "R"
        else -> ""
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.55f))
            .border(
                width = 0.5.dp,
                color = Color.White.copy(alpha = 0.7f),
                shape = RoundedCornerShape(16.dp),
            )
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            // Tilt dot — shifts horizontally with roll
            val dotOffset = (tilt.roll * 8f).coerceIn(-8f, 8f)
            Canvas(modifier = Modifier.size(6.dp).offset(x = dotOffset.dp)) {
                drawCircle(
                    color = if (abs(degrees) > 30) Color(0xFFFF6B6B) else Color(0xFF5B5FE6),
                    radius = size.minDimension / 2f,
                )
            }

            Text(
                text = "${abs(degrees)}°$direction",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2D2D2D).copy(alpha = 0.8f),
            )
        }
    }
}

@Composable
fun StickerVisual(
    type: StickerType,
    liftFraction: Float = 0f,
    tiltState: State<TiltData>? = null,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(
                when (type) {
                    StickerType.HELLO_TEXT -> 200.dp
                    StickerType.BUILD_ICON -> 170.dp
                    StickerType.CODE_ICON -> 150.dp
                    StickerType.KOTLIN_LOGO -> 100.dp
                    StickerType.HEART -> 90.dp
                    else -> 80.dp
                }
            )
            .stickerCutout(liftFraction = liftFraction)
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .let { mod -> if (tiltState != null) mod.holographicShine(tiltState) else mod },
    ) {
        when {
            type == StickerType.KOTLIN_LOGO -> KotlinLogoSticker()
            type == StickerType.HELLO_TEXT -> HelloTextSticker()
            type == StickerType.BUILD_ICON -> BuildIconSticker()
            type == StickerType.CODE_ICON -> CodeIconSticker()
            type.emoji != null -> Text(type.emoji, fontSize = 40.sp)
        }
    }
}

/**
 * Die-cut sticker outline effect with dynamic shadow.
 * [liftFraction] 0f = resting on surface, 1f = fully lifted during drag.
 * Shadow grows larger, darker, and more offset as the sticker lifts.
 */
private fun Modifier.stickerCutout(
    outlineWidth: androidx.compose.ui.unit.Dp = 3.dp,
    liftFraction: Float = 0f,
) = this.drawWithContent {
    val outlinePx = outlineWidth.toPx()
    val pad = outlinePx * 3 // extra padding for larger lifted shadow
    val layerBounds = Rect(-pad, -pad, size.width + pad, size.height + pad)

    // Shadow properties interpolated by liftFraction
    val shadowAlpha = 0.06f + liftFraction * 0.06f      // 0.06 → 0.12
    val shadowSpread = outlinePx + (1.dp.toPx() + liftFraction * 3.dp.toPx()) // +1dp → +4dp
    val shadowOffsetY = 1.dp.toPx() + liftFraction * 3.dp.toPx()             // 1dp → 4dp

    val shadowSteps = 16
    val shadowPaint = Paint().apply {
        colorFilter = ColorFilter.tint(Color.Black.copy(alpha = shadowAlpha / 2f), BlendMode.SrcIn)
    }
    for (i in 0 until shadowSteps) {
        val angle = (2.0 * PI * i / shadowSteps).toFloat()
        val dx = shadowSpread * cos(angle)
        val dy = shadowSpread * sin(angle) + shadowOffsetY
        drawIntoCanvas { canvas ->
            canvas.save()
            canvas.translate(dx, dy)
            canvas.saveLayer(layerBounds, shadowPaint)
        }
        drawContent()
        drawIntoCanvas { canvas ->
            canvas.restore()
            canvas.restore()
        }
    }

    // White outline layer: content drawn at offsets with white color filter
    val whitePaint = Paint().apply {
        colorFilter = ColorFilter.tint(Color.White, BlendMode.SrcIn)
    }
    val steps = 16
    for (i in 0 until steps) {
        val angle = (2.0 * PI * i / steps).toFloat()
        val dx = outlinePx * cos(angle)
        val dy = outlinePx * sin(angle)
        drawIntoCanvas { canvas ->
            canvas.save()
            canvas.translate(dx, dy)
            canvas.saveLayer(layerBounds, whitePaint)
        }
        drawContent()
        drawIntoCanvas { canvas ->
            canvas.restore()
            canvas.restore()
        }
    }

    // Actual content on top
    drawContent()
}

@Composable
private fun KotlinLogoSticker() {
    Canvas(modifier = Modifier.size(75.dp)) {
        val w = size.width
        val h = size.height
        // Kotlin diamond shape
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(w, 0f)
            lineTo(w * 0.5f, h * 0.5f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(
            path = path,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF7F52FF), Color(0xFFE44857), Color(0xFFFF8A00)),
                start = Offset(0f, 0f),
                end = Offset(w, h),
            ),
        )
    }
}

@Composable
private fun HelloTextSticker() {
    Text(
        text = "hello",
        fontSize = 52.sp,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        color = Color(0xFF2D2D2D),
    )
}

@Composable
private fun BuildIconSticker() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF00D2FF), Color(0xFF0B84FF)),
                ),
                shape = RoundedCornerShape(20.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Build,
            contentDescription = "Build",
            tint = Color.White,
            modifier = Modifier.size(72.dp),
        )
    }
}

@Composable
private fun CodeIconSticker() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2)),
                ),
                shape = RoundedCornerShape(20.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Code,
            contentDescription = "Code",
            tint = Color.White,
            modifier = Modifier.size(64.dp),
        )
    }
}
