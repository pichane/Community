package com.example.myapp.presentation.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

// Shared element global state manager
object SharedElementManager {
    val elements = mutableMapOf<String, SharedElementInfo>()
}

data class SharedElementInfo(
    val key: String,
    val screenKey: String,
    var size: IntSize = IntSize(0, 0),
    var position: IntOffset = IntOffset(0, 0)
)

// Extension function to mark and track shared elements across screens
fun Modifier.sharedElement(
    key: String,
    screenKey: String
): Modifier = composed {
    val density = LocalDensity.current
    var elementInfo by remember {
        mutableStateOf(
            SharedElementInfo(key, screenKey).also {
                SharedElementManager.elements[key + screenKey] = it
            }
        )
    }

    val otherScreenKey = if (screenKey == "home") "detail" else "home"
    val otherElement = SharedElementManager.elements[key + otherScreenKey]

    // Ensure we're tracking element position properly
    this.onGloballyPositioned { coordinates ->
        // Update position and size
        elementInfo = elementInfo.copy(
            size = coordinates.size,
            position = IntOffset(
                coordinates.positionInWindow().x.toInt(),
                coordinates.positionInWindow().y.toInt()
            )
        )

        // Update the shared manager
        SharedElementManager.elements[key + screenKey] = elementInfo
    }.let { modifier ->
        if (otherElement != null &&
            otherElement.size.width > 0 &&
            elementInfo.size.width > 0) {

            // Calculate transformation carefully to avoid division by zero
            val scaleX = if (elementInfo.size.width > 0)
                otherElement.size.width.toFloat() / elementInfo.size.width.toFloat() else 1f
            val scaleY = if (elementInfo.size.height > 0)
                otherElement.size.height.toFloat() / elementInfo.size.height.toFloat() else 1f

            // Calculate position offset
            val offsetX = otherElement.position.x - elementInfo.position.x
            val offsetY = otherElement.position.y - elementInfo.position.y

            // Use animateFloatAsState for smoother animations
            val animatedScale by animateFloatAsState(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                ),
                label = "scaleAnimation"
            )

            // Apply transformation with animation
            modifier
                .scale(
                    scaleX = 1f, // Apply scale directly in the initial setup
                    scaleY = 1f
                )
                .offset(
                    x = with(density) { 0.dp },
                    y = with(density) { 0.dp }
                )
        } else {
            modifier
        }
    }
}
