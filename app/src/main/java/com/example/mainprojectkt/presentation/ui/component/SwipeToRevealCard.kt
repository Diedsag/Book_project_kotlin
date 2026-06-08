package com.example.mainprojectkt.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.example.mainprojectkt.domain.model.Book
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.roundToInt

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun SwipeToRevealCard(
    book: Book,
    onEdit: (Book) -> Unit,
    onDelete: (Book) -> Unit,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val maxSwipeWidth = 100.dp
    val maxSwipePx = with(density) { maxSwipeWidth.toPx() }
    var actionsWidthPx by remember { mutableFloatStateOf(maxSwipePx) }
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val anchors = remember(actionsWidthPx) {
        mapOf(
            0f to 0,
            -actionsWidthPx to 1
        )
    }

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { size ->
                    actionsWidthPx = min(size.width.toFloat(), maxSwipePx)
                },
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = {
                    onEdit(book)
                    coroutineScope.launch { swipeableState.animateTo(0) }
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Изменить",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            IconButton(
                onClick = {
                    onDelete(book)
                    coroutineScope.launch { swipeableState.animateTo(0) }
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                .background(MaterialTheme.colorScheme.surface)
                .clickable {
                    if (swipeableState.currentValue == 1) {
                        coroutineScope.launch { swipeableState.animateTo(0) }
                    }
                }
        ) {
            content()
        }
    }
}