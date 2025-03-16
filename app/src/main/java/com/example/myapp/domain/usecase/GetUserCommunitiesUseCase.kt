package com.example.myapp.domain.usecase

import com.example.myapp.domain.model.Community
import com.example.myapp.domain.repository.SocialRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting communities that the current user has joined
 */
class GetUserCommunitiesUseCase(private val socialRepository: SocialRepository) {
    /**
     * Returns a Flow of Communities that the user has joined
     */
    operator fun invoke(): Flow<List<Community>> {
        return socialRepository.getUserCommunities()
    }
}