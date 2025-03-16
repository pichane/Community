package com.example.myapp.domain.usecase

import com.example.myapp.domain.repository.SocialRepository

/**
 * Use case for clearing the current photo selection
 */
class ClearPhotoSelectionUseCase(private val socialRepository: SocialRepository) {
    /**
     * Clears any selected missing photo information
     */
    suspend operator fun invoke() {
        socialRepository.clearPhotoSelection()
    }
}