package com.example.myapp.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import com.example.myapp.domain.repository.CameraRepository
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CameraRepositoryImpl(
    private val context: Context
) : CameraRepository {

    override suspend fun takePhoto(imageCapture: ImageCapture): Uri = suspendCancellableCoroutine { continuation ->
        try {
            val photoFile = createPhotoFile()
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val savedUri = Uri.fromFile(photoFile)
                        continuation.resume(savedUri)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("CameraRepository", "Photo capture failed", exception)
                        continuation.resumeWithException(exception)
                    }
                }
            )
        } catch (e: Exception) {
            Log.e("CameraRepository", "Photo capture setup failed", e)
            continuation.resumeWithException(e)
        }
    }

    private fun createPhotoFile(): File {
        return File(
            context.cacheDir,
            SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
                .format(System.currentTimeMillis()) + ".jpg"
        )
    }
}