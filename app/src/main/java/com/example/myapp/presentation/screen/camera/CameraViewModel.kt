package com.example.myapp.presentation.screen.camera

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.domain.model.Photo
import com.example.myapp.domain.usecase.SavePhotoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraViewModel(
    private val savePhotoUseCase: SavePhotoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    fun onEvent(event: CameraEvent) {
        when (event) {
            is CameraEvent.PhotoCaptured -> savePhoto(event.uri)
            is CameraEvent.SetCameraReady -> setCameraReady(event.isReady)
            is CameraEvent.SetCapturing -> setCapturing(event.isCapturing)
            is CameraEvent.ErrorOccurred -> handleError(event.message)
            CameraEvent.ErrorShown -> clearError()
        }
    }

    private fun savePhoto(uri: Uri) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isCapturing = true) }

                val timestamp = System.currentTimeMillis()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val title = "Photo ${dateFormat.format(Date(timestamp))}"

                val photo = Photo(
                    uri = uri,
                    timestamp = timestamp,
                    title = title
                )

                savePhotoUseCase(photo)
                _uiState.update {
                    it.copy(
                        isCapturing = false,
                        lastCapturedPhotoUri = uri,
                        lastPhotoTimestamp = timestamp
                    )
                }
            } catch (e: Exception) {
                handleError("Failed to save photo: ${e.message}")
            }
        }
    }

    private fun setCameraReady(isReady: Boolean) {
        _uiState.update { it.copy(isCameraReady = isReady) }
    }

    private fun setCapturing(isCapturing: Boolean) {
        _uiState.update { it.copy(isCapturing = isCapturing) }
    }

    private fun handleError(message: String) {
        _uiState.update {
            it.copy(
                error = message,
                isCapturing = false
            )
        }
    }

    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class CameraUiState(
    val isCameraReady: Boolean = false,
    val isCapturing: Boolean = false,
    val error: String? = null,
    val lastCapturedPhotoUri: Uri? = null,
    val lastPhotoTimestamp: Long = 0L,
    val hasCameraPermission: Boolean = false
)

sealed class CameraEvent {
    data class PhotoCaptured(val uri: Uri) : CameraEvent()
    data class SetCameraReady(val isReady: Boolean) : CameraEvent()
    data class SetCapturing(val isCapturing: Boolean) : CameraEvent()
    data class ErrorOccurred(val message: String) : CameraEvent()
    object ErrorShown : CameraEvent()
}
