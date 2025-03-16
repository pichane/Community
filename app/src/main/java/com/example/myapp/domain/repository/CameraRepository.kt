package com.example.myapp.domain.repository

import android.net.Uri
import androidx.camera.core.ImageCapture

interface CameraRepository {
    suspend fun takePhoto(imageCapture: ImageCapture): Uri
}