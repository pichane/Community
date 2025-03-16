package com.example.myapp.presentation.screen.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry

/**
 * Animation utilities for screen transitions
 */
object SharedElementTransition {
    
    private const val ANIMATION_DURATION = 500
    
    // Enter transition when navigating from home to detail
    fun enterTransition(scope: AnimatedContentTransitionScope<NavBackStackEntry>): EnterTransition {
        return fadeIn(animationSpec = tween(ANIMATION_DURATION))
    }
    
    // Exit transition when navigating from home to detail
    fun exitTransition(scope: AnimatedContentTransitionScope<NavBackStackEntry>): ExitTransition {
        return fadeOut(animationSpec = tween(ANIMATION_DURATION))
    }
    
    // Enter transition when navigating back from detail to home
    fun popEnterTransition(scope: AnimatedContentTransitionScope<NavBackStackEntry>): EnterTransition {
        return fadeIn(animationSpec = tween(ANIMATION_DURATION))
    }
    
    // Exit transition when navigating back from detail to home
    fun popExitTransition(scope: AnimatedContentTransitionScope<NavBackStackEntry>): ExitTransition {
        return fadeOut(animationSpec = tween(ANIMATION_DURATION))
    }
}