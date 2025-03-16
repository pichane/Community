package com.example.myapp.domain.usecase

import com.example.myapp.domain.model.MissingPhotoInfo
import com.example.myapp.domain.repository.SocialRepository

/**
 * Use case for retrieving the currently selected missing photo
 */
class GetSelectedMissingPhotoUseCase(private val socialRepository: SocialRepository) {
    /**
     * Returns the currently selected missing photo information, or null if none selected
     */
    suspend operator fun invoke(): MissingPhotoInfo? {
        return socialRepository.getSelectedMissingPhoto()
    }
}