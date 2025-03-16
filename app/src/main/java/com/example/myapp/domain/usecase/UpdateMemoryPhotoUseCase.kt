package com.example.myapp.domain.usecase

import com.example.myapp.domain.repository.SocialRepository

class UpdateMemoryPhotoUseCase(private val socialRepository: SocialRepository) {

    suspend operator fun invoke(
        communityId: String,
        memoryId: String,
        photoId: String,
        newPhotoUrl: String
    ) {
        socialRepository.updateMemoryPhoto(communityId, memoryId, photoId, newPhotoUrl)
    }
}