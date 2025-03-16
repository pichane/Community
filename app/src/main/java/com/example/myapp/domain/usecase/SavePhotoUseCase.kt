package com.example.myapp.domain.usecase

import com.example.myapp.domain.model.Photo
import com.example.myapp.domain.repository.PhotoRepository

class SavePhotoUseCase(private val photoRepository: PhotoRepository) {
    suspend operator fun invoke(photo: Photo): Long {
        return photoRepository.savePhoto(photo)
    }
}