package com.example.myapp.domain.usecase

import com.example.myapp.domain.model.MissingPhotoInfo
import com.example.myapp.domain.repository.SocialRepository

/**
 * Use case for selecting a missing photo for camera capture
 */
class SelectMissingPhotoUseCase(private val socialRepository: SocialRepository) {
    /**
     * Stores the selected missing photo information
     */
    suspend operator fun invoke(missingPhotoInfo: MissingPhotoInfo) {
        socialRepository.selectMissingPhotoForCapture(missingPhotoInfo)
    }
}