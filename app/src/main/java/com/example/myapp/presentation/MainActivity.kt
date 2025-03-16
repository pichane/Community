package com.example.myapp.presentation

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapp.presentation.screen.camera.CameraScreen
import com.example.myapp.presentation.screen.home.HomeScreen
import com.example.myapp.presentation.screen.memory.PhotoCompositionScreen
import com.example.myapp.presentation.screen.navigation.SharedElementTransition
import com.example.myapp.presentation.screen.onboarding.OnBoardingScreen
import com.example.myapp.presentation.screen.photodetail.PhotoDetailScreen
import com.example.myapp.presentation.screen.social.SocialScreen
import com.example.myapp.presentation.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    NavHost(navController = navController, startDestination = "onboarding") {
        composable("onboarding") {
            OnBoardingScreen(
                onGetStartedClick = {
                    navController.navigate("home") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "home"
        ) {
            HomeScreen(
                onTakePhotoClick = {
                    navController.navigate("camera")
                },
                onPhotoClick = { photoId ->
                    navController.navigate("photo_detail/$photoId")
                },
                onSocialClick = {
                    navController.navigate("social")
                },
                onMemoryClick = { communityId, memoryId ->
                    navController.navigate("memory_detail/${communityId}/${memoryId}")
                }
            )
        }

        composable(
            route = "social",
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            }
        ) {
            SocialScreen(onBack = { navController.popBackStack() })
        }


        composable("camera") {
            CameraScreen(
                onPhotoTaken = { uri ->
                    capturedImageUri = uri
                    navController.popBackStack()
                },
                onBack = {

                    Log.e("yallah", " clicked MainActivity")
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "photo_detail/{photoId}",
            arguments = listOf(navArgument("photoId") { type = NavType.IntType }),
            enterTransition = { SharedElementTransition.enterTransition(this) },
            exitTransition = { SharedElementTransition.exitTransition(this) },
            popEnterTransition = { SharedElementTransition.popEnterTransition(this) },
            popExitTransition = { SharedElementTransition.popExitTransition(this) }
        ) { backStackEntry ->
            val photoId = backStackEntry.arguments?.getInt("photoId") ?: -1

            PhotoDetailScreen(
                photoId = photoId,
                onBack = {
                    navController.popBackStack()
                },
                onDelete = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "memory_detail/{communityId}/{memoryId}",
            arguments = listOf(
                navArgument("communityId") { type = NavType.StringType },
                navArgument("memoryId") { type = NavType.StringType }
            ),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) { backStackEntry ->
            val communityId = backStackEntry.arguments?.getString("communityId") ?: ""
            val memoryId = backStackEntry.arguments?.getString("memoryId") ?: ""

            PhotoCompositionScreen(
                communityId = communityId,
                memoryId = memoryId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
