package com.example.myapp.domain.usecase

import com.example.myapp.domain.repository.SocialRepository

class UpdateMemoryPhotoUseCase(private val socialRepository: SocialRepository) {
    /**
     * Updates the photo URL of a specific user's photo in a memory
     * 
     * @param communityId The ID of the community
     * @param memoryId The ID of the memory
     * @param photoId The ID of the photo
     * @param newPhotoUrl The new photo URL to set
     */
    suspend operator fun invoke(communityId: String, memoryId: String, photoId: String, newPhotoUrl: String) {
        socialRepository.updateMemoryPhoto(communityId, memoryId, photoId, newPhotoUrl)
    }
}