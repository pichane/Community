package com.example.myapp.presentation.screen.camera

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.myapp.presentation.common.debouncedClickable
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraContent(
    hasCameraPermission: Boolean,
    uiState: CameraUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onCapturePhoto: (ImageCapture) -> Unit,
    onCameraReady: (Boolean) -> Unit
) {
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    Scaffold(
        topBar = { CameraTopBar(onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (hasCameraPermission) {
                CameraPreviewContent(
                    onUseCase = { useCaseImageCapture ->
                        imageCapture = useCaseImageCapture
                        onCameraReady(true)
                    }
                )

                CameraControls(
                    isCapturing = uiState.isCapturing,
                    isCameraReady = uiState.isCameraReady,
                    lastPhotoTimestamp = uiState.lastPhotoTimestamp,
                    onTakePhoto = {
                        imageCapture?.let { capture ->
                            onCapturePhoto(capture)
                        }
                    }
                )
            } else {
                PermissionRequiredMessage()
            }
        }
    }

    // Clean up camera resources when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            imageCapture = null
            onCameraReady(false)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = { Text("Take Photo") },
        navigationIcon = {
            Text("Back",
                modifier = Modifier.debouncedClickable {
                    onBack()
                }
            )
        }
    )
}

@Composable
private fun CameraPreviewContent(
    onUseCase: (ImageCapture) -> Unit
) {
    CameraPreview(
        modifier = Modifier.fillMaxSize(),
        onUseCase = onUseCase
    )
}

@Composable
private fun PermissionRequiredMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Camera permission is required to use this feature",
            modifier = Modifier
                .padding(16.dp)
        )
    }
}

@Composable
private fun CameraControls(
    isCapturing: Boolean,
    isCameraReady: Boolean,
    lastPhotoTimestamp: Long,
    onTakePhoto: () -> Unit
) {
    // Show loading indicator during capture
    if (isCapturing) {
        CaptureLoadingIndicator()
    }

    // Capture button
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 32.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        FloatingActionButton(
            onClick = {
                // Debounce logic to prevent multiple rapid clicks
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastPhotoTimestamp > 1000) {
                    onTakePhoto()
                }
            },

            ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Take Photo"
            )
        }
    }
}

@Composable
private fun CaptureLoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.White
        )
    }
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onUseCase: (ImageCapture) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember { PreviewView(context) }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    // Handle camera binding with lifecycle
    DisposableEffect(lifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val cameraProviderListener = Runnable {
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )

                onUseCase(imageCapture)
            } catch (e: Exception) {
                Log.e("CameraPreview", "Use case binding failed", e)
            }
        }

        cameraProviderFuture.addListener(
            cameraProviderListener,
            ContextCompat.getMainExecutor(context)
        )

        onDispose {
            try {
                // Clean up by unbinding camera use cases
                val cameraProvider = cameraProviderFuture.get()
                cameraProvider.unbindAll()
            } catch (e: Exception) {
                Log.e("CameraPreview", "Failed to unbind camera use cases", e)
            }
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
}
