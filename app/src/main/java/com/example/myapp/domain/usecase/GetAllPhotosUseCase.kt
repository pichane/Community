package com.example.myapp.domain.usecase

import com.example.myapp.domain.model.Photo
import com.example.myapp.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow

class GetAllPhotosUseCase(private val photoRepository: PhotoRepository) {
    operator fun invoke(): Flow<List<Photo>> {
        return photoRepository.getAllPhotos()
    }
}