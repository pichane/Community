package com.example.myapp.domain.usecase

import com.example.myapp.domain.model.FromUser
import com.example.myapp.domain.repository.SocialRepository
import kotlinx.coroutines.flow.first


class GetCommunityPhotosUseCase(private val socialRepository: SocialRepository) {

    suspend operator fun invoke(communityId: String, memoryIndex: Int): List<FromUser> {
        // Get the community from the repository
        val community = socialRepository.getUserCommunities()
            .first()
            .find { it.id == communityId }

        // If community not found or no memories, return empty list
        if (community == null || community.memories.isEmpty() || memoryIndex >= community.memories.size) {
            return emptyList()
        }

        // Return the photos from the specified memory
        return community.memories[memoryIndex].photos
    }
}