package com.example.stickerexplode.model

import kotlinx.serialization.Serializable

@Serializable
enum class StickerType(val label: String, val emoji: String?) {
    KOTLIN_LOGO("Kotlin", null),
    GIFT("Gift", "\uD83C\uDF81"),
    DEVELOPER("Dev", "\uD83D\uDC69\u200D\uD83D\uDCBB"),
    HELLO_TEXT("Hello", null),
    BUILD_ICON("Build", null),
    CODE_ICON("Code", null),
    HEART("Heart", "\uD83D\uDE3B"),
    STAR("Star", "⭐"),
    FIRE("Fire", "\uD83D\uDD25"),
    ROCKET("Rocket", "\uD83D\uDE80"),
    SPARKLES("Sparkles", "✨"),
    PARTY("Party", "\uD83C\uDF89"),
    THUMBS_UP("Thumbs Up", "\uD83D\uDC4D"),
    LIGHTNING("Lightning", "⚡"),
    RAINBOW("Rainbow", "\uD83C\uDF08"),
    EYES("Eyes", "\uD83D\uDC40"),
}

@Serializable
data class StickerItem(
    val id: Int,
    val type: StickerType,
    val initialFractionX: Float,
    val initialFractionY: Float,
    val rotation: Float = 0f,
    val offsetX: Float = Float.NaN,
    val offsetY: Float = Float.NaN,
    val pinchScale: Float = 1f,
    val zIndex: Float = 0f,
)

val defaultStickers = listOf(
    StickerItem(0, StickerType.KOTLIN_LOGO, 0.38f, 0.05f, 5f),
    StickerItem(1, StickerType.GIFT, 0.08f, 0.18f, -5f),
    StickerItem(2, StickerType.DEVELOPER, 0.55f, 0.17f, 3f),
    StickerItem(3, StickerType.HELLO_TEXT, 0.15f, 0.30f, -2f),
    StickerItem(4, StickerType.BUILD_ICON, -0.08f, 0.55f, -15f),
    StickerItem(5, StickerType.CODE_ICON, 0.55f, 0.48f, 8f),
    StickerItem(6, StickerType.HEART, 0.30f, 0.65f, -3f),
)
