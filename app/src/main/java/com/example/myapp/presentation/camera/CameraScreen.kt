package com.example.myapp.presentation.camera

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
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.myapp.presentation.common.debouncedClickable
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


import kotlinx.coroutines.delay
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    onPhotoTaken: (Uri) -> Unit,
    onBack: () -> Unit,
    viewModel: CameraViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Track permission state
    var hasCameraPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    // Track camera state
    var isCameraReady by remember { mutableStateOf(false) }
    var isCapturing by remember { mutableStateOf(false) }

    // Keep a reference to the latest callbacks
    val currentOnPhotoTaken by rememberUpdatedState(onPhotoTaken)

    // Remember the last time photo was taken to prevent rapid multiple clicks
    val lastPhotoTimestamp = remember { mutableStateOf(0L) }

    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    val executor = remember { ContextCompat.getMainExecutor(context) }

    // Request permission when the screen is first displayed
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // Clean up camera resources when leaving the screen
    DisposableEffect(lifecycleOwner) {
        onDispose {
            imageCapture = null
            isCameraReady = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Take Photo") },
                navigationIcon = {
                        Text("Back", modifier = Modifier.debouncedClickable { onBack() })
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (hasCameraPermission) {
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    onUseCase = { useCase ->
                        imageCapture = useCase
                        isCameraReady = true
                    }
                )

                // Show loading indicator during capture
                if (isCapturing) {
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

                Button(
                    onClick = {
                        // Debounce logic to prevent multiple rapid clicks
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastPhotoTimestamp.value > 1000 && !isCapturing && isCameraReady) {
                            lastPhotoTimestamp.value = currentTime
                            isCapturing = true

                            val capture = imageCapture
                            if (capture != null) {
                                takePhoto(
                                    context = context,
                                    imageCapture = capture,
                                    executor = executor,
                                    onSuccess = { uri ->
                                        // Save photo to database
                                        viewModel.savePhoto(uri)
                                        // Reset capturing flag
                                        isCapturing = false
                                        // Navigate back
                                        currentOnPhotoTaken(uri)
                                    },
                                    onError = { exception ->
                                        Log.e("CameraScreen", "Error capturing photo", exception)
                                        isCapturing = false
                                    }
                                )
                            } else {
                                // Camera not ready yet
                                isCapturing = false
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp),
                    // Disable button when camera is not ready or is currently capturing
                    enabled = isCameraReady && !isCapturing
                ) {
                    Text("Take Photo")
                }
            } else {
                Text(
                    text = "Camera permission is required to use this feature",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
        }
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

        cameraProviderFuture.addListener(cameraProviderListener, ContextCompat.getMainExecutor(context))

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

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    executor: Executor,
    onSuccess: (Uri) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        val photoFile = File(
            context.cacheDir,
            SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
                .format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    onSuccess(savedUri)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("TakePhoto", "Photo capture failed", exception)
                    onError(exception)
                }
            }
        )
    } catch (e: Exception) {
        Log.e("TakePhoto", "Photo capture setup failed", e)
        onError(e)
    }
}

// Use extension function instead of suspend function for better error handling
suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

    cameraProviderFuture.addListener({
        try {
            continuation.resume(cameraProviderFuture.get())
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }, ContextCompat.getMainExecutor(this))
}