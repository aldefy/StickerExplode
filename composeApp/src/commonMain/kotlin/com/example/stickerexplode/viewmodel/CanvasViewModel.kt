package com.example.stickerexplode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stickerexplode.data.CanvasRepository
import com.example.stickerexplode.data.CanvasState
import com.example.stickerexplode.model.HistoryEntry
import com.example.stickerexplode.model.StickerItem
import com.example.stickerexplode.model.StickerType
import com.example.stickerexplode.model.defaultStickers
import com.example.stickerexplode.util.currentTimeMillis
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class CanvasViewModel(private val repository: CanvasRepository) : ViewModel() {

    private val _stickers = MutableStateFlow<List<StickerItem>>(emptyList())
    val stickers: StateFlow<List<StickerItem>> = _stickers.asStateFlow()

    private val _history = MutableStateFlow<List<HistoryEntry>>(emptyList())
    val history: StateFlow<List<HistoryEntry>> = _history.asStateFlow()

    private val _sensorEnabled = MutableStateFlow(true)
    val sensorEnabled: StateFlow<Boolean> = _sensorEnabled.asStateFlow()

    private var nextId = 0
    private var zCounter = 0
    private var saveJob: Job? = null

    init {
        viewModelScope.launch {
            val saved = repository.loadCanvasState()
            if (saved != null && saved.stickers.isNotEmpty()) {
                _stickers.value = saved.stickers
                _history.value = saved.history
                _sensorEnabled.value = saved.sensorEnabled
                nextId = saved.nextId
                zCounter = saved.zCounter
            } else {
                _stickers.value = defaultStickers
                nextId = defaultStickers.size
            }
        }
    }

    fun addSticker(type: StickerType) {
        val randomX = 0.15f + Random.nextFloat() * 0.5f
        val randomY = 0.2f + Random.nextFloat() * 0.4f
        val randomRotation = -15f + Random.nextFloat() * 30f
        val id = nextId++
        zCounter++
        val sticker = StickerItem(
            id = id,
            type = type,
            initialFractionX = randomX,
            initialFractionY = randomY,
            rotation = randomRotation,
            zIndex = zCounter.toFloat(),
        )
        _stickers.value = _stickers.value + sticker
        _history.value = _history.value + HistoryEntry(
            stickerType = type,
            timestampMillis = currentTimeMillis(),
        )
        debouncedSave()
    }

    fun updateStickerTransform(
        id: Int,
        offsetX: Float,
        offsetY: Float,
        scale: Float,
        rotation: Float,
    ) {
        _stickers.value = _stickers.value.map { sticker ->
            if (sticker.id == id) {
                sticker.copy(
                    offsetX = offsetX,
                    offsetY = offsetY,
                    pinchScale = scale,
                    rotation = rotation,
                )
            } else sticker
        }
        debouncedSave()
    }

    fun bringToFront(id: Int) {
        zCounter++
        _stickers.value = _stickers.value.map { sticker ->
            if (sticker.id == id) sticker.copy(zIndex = zCounter.toFloat()) else sticker
        }
        debouncedSave()
    }

    fun toggleSensor() {
        _sensorEnabled.value = !_sensorEnabled.value
        debouncedSave()
    }

    private fun debouncedSave() {
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(500)
            repository.saveCanvasState(
                CanvasState(
                    stickers = _stickers.value,
                    history = _history.value,
                    nextId = nextId,
                    zCounter = zCounter,
                    sensorEnabled = _sensorEnabled.value,
                )
            )
        }
    }
}
