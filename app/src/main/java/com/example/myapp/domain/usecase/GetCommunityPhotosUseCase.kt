package com.example.myapp.domain.usecase

import com.example.myapp.domain.model.Community
import com.example.myapp.domain.model.FromUser
import com.example.myapp.domain.model.Memory
import com.example.myapp.domain.repository.SocialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

/**
 * Use case for getting photos from a specific community memory
 */
class GetCommunityPhotosUseCase(private val socialRepository: SocialRepository) {
    /**
     * Gets photos for a specific community memory
     * 
     * @param communityId The ID of the community
     * @param memoryIndex The index of the memory to fetch (0 for first, 1 for second, etc.)
     * @return List of FromUser objects representing photos in the memory
     */
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