package com.example.stickerexplode

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stickerexplode.haptics.HapticFeedbackProvider
import com.example.stickerexplode.haptics.HapticType
import com.example.stickerexplode.model.StickerType
import com.example.stickerexplode.sensor.TiltData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StickerTray(
    haptics: HapticFeedbackProvider,
    onStickerSelected: (StickerType) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showSheet by remember { mutableStateOf(false) }

    // FAB to open the tray
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd,
    ) {
        FloatingActionButton(
            onClick = {
                haptics.perform(HapticType.SelectionClick)
                showSheet = true
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
                imageVector = Icons.Filled.Add,
                contentDescription = "Add sticker",
                modifier = Modifier.size(28.dp),
            )
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            dragHandle = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFFDDDDDD)),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Add Sticker",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2D2D2D),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            },
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
            ) {
                items(StickerType.entries.toList()) { type ->
                    StickerTrayItem(
                        type = type,
                        onClick = {
                            haptics.perform(HapticType.LightTap)
                            onStickerSelected(type)
                            showSheet = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun StickerTrayItem(
    type: StickerType,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
    )
    val bgColor by animateColorAsState(
        targetValue = if (isPressed) Color(0xFFE8E8FF) else Color(0xFFF5F5FA),
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(48.dp),
        ) {
            StickerVisual(type = type)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = type.label,
            fontSize = 11.sp,
            color = Color(0xFF888888),
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}
