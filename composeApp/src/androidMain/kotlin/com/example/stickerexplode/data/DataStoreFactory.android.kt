package com.example.stickerexplode.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

private lateinit var appDataStore: DataStore<Preferences>

fun initDataStore(filesDir: String) {
    if (!::appDataStore.isInitialized) {
        appDataStore = createDataStore { "$filesDir/$DATA_STORE_FILE_NAME" }
    }
}

actual fun createPlatformDataStore(): DataStore<Preferences> = appDataStore
