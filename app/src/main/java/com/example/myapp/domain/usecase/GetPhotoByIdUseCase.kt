package com.example.myapp.domain.usecase

import com.example.myapp.domain.model.Photo
import com.example.myapp.domain.repository.PhotoRepository

class GetPhotoByIdUseCase(private val photoRepository: PhotoRepository) {
    suspend operator fun invoke(photoId: Int): Photo? {
        return photoRepository.getPhotoById(photoId)
    }
}