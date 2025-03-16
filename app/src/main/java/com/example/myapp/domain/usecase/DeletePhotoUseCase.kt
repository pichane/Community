package com.example.myapp.domain.usecase

import com.example.myapp.domain.repository.PhotoRepository

class DeletePhotoUseCase(private val photoRepository: PhotoRepository) {
    suspend operator fun invoke(photoId: Int) {
        return photoRepository.deletePhoto(photoId)
    }
}