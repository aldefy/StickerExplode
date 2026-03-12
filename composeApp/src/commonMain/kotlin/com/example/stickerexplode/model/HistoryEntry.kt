package com.example.stickerexplode.model

import kotlinx.serialization.Serializable

@Serializable
data class HistoryEntry(
    val stickerType: StickerType,
    val timestampMillis: Long,
)
