package com.example.stickerexplode

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.stickerexplode.data.initDataStore
import com.example.stickerexplode.model.StickerType

@Preview(widthDp = 412, heightDp = 915)
@Composable
fun AppPreview() {
    initDataStore(System.getProperty("java.io.tmpdir") ?: "/tmp")
    App()
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
