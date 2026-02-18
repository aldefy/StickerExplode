package com.example.stickerexplode.model

enum class StickerType(val label: String, val emoji: String?) {
    KOTLIN_LOGO("Kotlin", null),
    GIFT("Gift", "🎁"),
    DEVELOPER("Dev", "👩‍💻"),
    HELLO_TEXT("Hello", null),
    BUILD_ICON("Build", null),
    CODE_ICON("Code", null),
    HEART("Heart", "😻"),
    STAR("Star", "⭐"),
    FIRE("Fire", "🔥"),
    ROCKET("Rocket", "🚀"),
    SPARKLES("Sparkles", "✨"),
    PARTY("Party", "🎉"),
    THUMBS_UP("Thumbs Up", "👍"),
    LIGHTNING("Lightning", "⚡"),
    RAINBOW("Rainbow", "🌈"),
    EYES("Eyes", "👀"),
}

data class StickerItem(
    val id: Int,
    val type: StickerType,
    val initialFractionX: Float,
    val initialFractionY: Float,
    val rotation: Float = 0f,
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
