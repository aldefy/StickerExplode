package com.example.stickerexplode

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.stickerexplode.data.CanvasRepository
import com.example.stickerexplode.data.createPlatformDataStore
import com.example.stickerexplode.viewmodel.CanvasViewModel

@Composable
fun App() {
    val dataStore = remember { createPlatformDataStore() }
    val repository = remember { CanvasRepository(dataStore) }
    val viewModel = remember { CanvasViewModel(repository) }
    val navController = rememberNavController()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        NavHost(navController = navController, startDestination = "canvas") {
            composable("canvas") {
                StickerCanvas(
                    viewModel = viewModel,
                    onNavigateToHistory = { navController.navigate("history") },
                )
            }
            composable("history") {
                HistoryScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
