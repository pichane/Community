package com.example.myapp.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val DarkColors = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFFBB86FC),
    onPrimary = androidx.compose.ui.graphics.Color.Black,
    secondary = androidx.compose.ui.graphics.Color(0xFF03DAC5),
    onSecondary = androidx.compose.ui.graphics.Color.Black,
    background = androidx.compose.ui.graphics.Color(0xFF121212),
    onBackground = androidx.compose.ui.graphics.Color.White,
    surface = androidx.compose.ui.graphics.Color(0xFF121212),
    onSurface = androidx.compose.ui.graphics.Color.White,
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF353A48),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFc4c7cf),
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val colors = DarkColors

    CompositionLocalProvider(
        LocalDimensions provides Dimensions(),
        content = content
    )
    MaterialTheme(
        colorScheme = colors,
        content = content,
    )
}
val MaterialTheme.dimensions: Dimensions
    @Composable
    get() = LocalDimensions.current