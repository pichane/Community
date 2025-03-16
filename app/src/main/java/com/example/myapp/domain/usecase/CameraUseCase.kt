package com.example.myapp.domain.usecase

import android.net.Uri
import androidx.camera.core.ImageCapture
import com.example.myapp.domain.repository.CameraRepository

class CapturePhotoUseCase(private val cameraRepository: CameraRepository) {
    suspend operator fun invoke(imageCapture: ImageCapture): Uri {
        return cameraRepository.takePhoto(imageCapture)
    }
}
