package com.example.myapp.presentation.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimensions(
    // Icons
    val iconSmall: Dp = 16.dp,
    val iconSmallPlus: Dp = 18.dp,
    val iconMedium: Dp = 24.dp,
    val iconMediumPlus: Dp = 28.dp,
    val iconLarge: Dp = 32.dp,
    val iconExtraLarge: Dp = 40.dp,
    val iconExtraLargePlus: Dp = 48.dp,
    val avatarSize: Dp = 56.dp,
    val iconVeryLarge: Dp = 64.dp,

    // Spacing
    val spaceExtraSmall: Dp = 4.dp,
    val spaceSmall: Dp = 8.dp,
    val spaceMedium: Dp = 16.dp,
    val spaceLarge: Dp = 24.dp,
    val spaceExtraLarge: Dp = 32.dp,
    
    // Button sizes
    val buttonHeight: Dp = 48.dp,
    
    // Other common dimensions
    val dividerThickness: Dp = 1.dp,
    val cardElevation: Dp = 4.dp
)

val LocalDimensions = compositionLocalOf { Dimensions() }