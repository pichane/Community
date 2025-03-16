package com.example.myapp.presentation.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

/**
 * Extension function to make a Modifier clickable with debounce protection
 * to prevent accidental double-clicks
 */
fun Modifier.debouncedClickable(
    debounceTime: Long = 300L,
    onClick: () -> Unit
): Modifier = composed {
    val lastClickTime = remember { java.util.concurrent.atomic.AtomicLong(0) }
    
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime.get() >= debounceTime) {
            lastClickTime.set(currentTime)
            onClick()
        }
    }
}