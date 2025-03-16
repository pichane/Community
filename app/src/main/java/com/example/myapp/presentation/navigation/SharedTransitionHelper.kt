package com.example.myapp.presentation.navigation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * A helper class that manages shared element transitions using the latest Compose APIs
 */
class SharedTransitionHelper(private val navController: NavHostController) {

    // Specs for the shared element animation
    val sharedTransitionSpec = SpringSpec<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )

    @Composable
    fun getCurrentRoute(): String? {
        return navController.currentBackStackEntryAsState().value?.destination?.route
    }
}

// Composition local to access the shared transition helper from any composable
val LocalSharedTransitionHelper = staticCompositionLocalOf<SharedTransitionHelper?> { null }