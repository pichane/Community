package com.example.myapp.presentation.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed


fun Modifier.debouncedClickable(
    debounceTime: Long = 400L,
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