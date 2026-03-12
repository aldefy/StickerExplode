package com.example.stickerexplode.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.stickerexplode.model.HistoryEntry
import com.example.stickerexplode.model.StickerItem
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class CanvasState(
    val stickers: List<StickerItem> = emptyList(),
    val history: List<HistoryEntry> = emptyList(),
    val nextId: Int = 0,
    val zCounter: Int = 0,
    val sensorEnabled: Boolean = true,
)

class CanvasRepository(private val dataStore: DataStore<Preferences>) {

    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private val CANVAS_STATE_KEY = stringPreferencesKey("canvas_state")
    }

    suspend fun loadCanvasState(): CanvasState? {
        val prefs = dataStore.data.first()
        val raw = prefs[CANVAS_STATE_KEY] ?: return null
        return try {
            json.decodeFromString<CanvasState>(raw)
        } catch (_: Exception) {
            null
        }
    }

    suspend fun saveCanvasState(state: CanvasState) {
        dataStore.edit { prefs ->
            prefs[CANVAS_STATE_KEY] = json.encodeToString(state)
        }
    }
}
