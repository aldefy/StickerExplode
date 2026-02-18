package com.example.stickerexplode

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.stickerexplode.model.StickerType

@Preview
@Composable
fun AppPreview() {
    App()
}

@Preview
@Composable
fun StickerCanvasPreview() {
    StickerCanvas()
}

@Preview
@Composable
fun StickerVisualKotlinPreview() {
    StickerVisual(type = StickerType.KOTLIN_LOGO)
}

@Preview
@Composable
fun StickerVisualBuildPreview() {
    StickerVisual(type = StickerType.BUILD_ICON)
}

@Preview
@Composable
fun StickerVisualCodePreview() {
    StickerVisual(type = StickerType.CODE_ICON)
}
