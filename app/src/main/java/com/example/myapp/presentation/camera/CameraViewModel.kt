package com.example.myapp.presentation.camera

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.domain.model.Photo
import com.example.myapp.domain.usecase.SavePhotoUseCase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraViewModel(
    private val savePhotoUseCase: SavePhotoUseCase
) : ViewModel() {

    fun savePhoto(uri: Uri) {
        viewModelScope.launch {
            val timestamp = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val title = "Photo ${dateFormat.format(Date(timestamp))}"

            val photo = Photo(
                uri = uri,
                timestamp = timestamp,
                title = title
            )

            savePhotoUseCase(photo)
        }
    }
}