package com.example.myapp.presentation.screen.camera

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun CameraScreen(
    onPhotoTaken: (Uri) -> Unit,
    onBack: () -> Unit,
    viewModel: CameraViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Track permission state
    var hasCameraPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    // Keep a reference to the latest callbacks
    val currentOnPhotoTaken by rememberUpdatedState(onPhotoTaken)

    // Request permission when the screen is first displayed
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // Handle errors
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            scope.launch {
                snackBarHostState.showSnackbar(error)
                viewModel.onEvent(CameraEvent.ErrorShown)
            }
        }
    }

    // When a photo is successfully captured, invoke the callback
    LaunchedEffect(uiState.lastCapturedPhotoUri) {
        uiState.lastCapturedPhotoUri?.let { uri ->
            currentOnPhotoTaken(uri)
        }
    }

    CameraContent(
        hasCameraPermission = hasCameraPermission,
        uiState = uiState,
        snackbarHostState = snackBarHostState,
        onBack = onBack,
        onCapturePhoto = { imageCapture ->
            viewModel.onEvent(CameraEvent.PhotoCaptured(imageCapture))
        },
        onCameraReady = { isReady ->
            viewModel.onEvent(CameraEvent.SetCameraReady(isReady))
        }
    )
}
