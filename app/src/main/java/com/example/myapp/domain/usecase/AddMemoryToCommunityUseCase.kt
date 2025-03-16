package com.example.myapp.domain.usecase

import com.example.myapp.domain.model.FromUser
import com.example.myapp.domain.model.Memory
import com.example.myapp.domain.repository.SocialRepository
import kotlinx.coroutines.flow.first
import java.util.Date
import java.util.UUID

/**
 * Use case responsible for creating and adding a new memory to a community
 */
class AddMemoryToCommunityUseCase(private val socialRepository: SocialRepository) {
    /**
     * Creates a new memory for a community with empty photo slots for all members
     *
     * @param communityId ID of the community to add memory to
     * @param title Title of the new memory
     * @param date Date of the memory event
     */
    suspend operator fun invoke(communityId: String, title: String, date: Date) {
        // Get community to find users
        val community = socialRepository.getUserCommunities()
            .first()
            .find { it.id == communityId } ?: return

        // Extract unique user IDs from existing memories
        val usersInCommunity = extractUniqueUserIds(community)
        
        // Create empty photos for each user
        val newPhotos = createEmptyPhotos(usersInCommunity)
        
        // Determine if this memory should be horizontal or vertical
        // Alternate based on existing memories count
        val isHorizontal = community.memories.size % 2 != 0
        
        // Create the new memory
        val newMemory = buildMemory(communityId, title, isHorizontal, newPhotos)
        
        // Add the new memory to the community
        socialRepository.addMemoryToCommunity(communityId, newMemory)
    }
    
    /**
     * Extract unique user IDs from a community's memories
     */
    private fun extractUniqueUserIds(community: com.example.myapp.domain.model.Community): Set<String> {
        val userIds = mutableSetOf<String>()
        
        // Always include the main user
        userIds.add("user_main")
        
        // Add all users referenced in existing memories
        community.memories.forEach { memory ->
            memory.photos.forEach { photo ->
                userIds.add(photo.userId)
            }
        }
        
        return userIds
    }
    
    /**
     * Create empty photo slots for each user
     */
    private fun createEmptyPhotos(userIds: Set<String>): List<FromUser> {
        return userIds.map { userId ->
            FromUser(
                id = "new_photo_${UUID.randomUUID()}",
                userId = userId,
                url = "" // Empty URL for new memory
            )
        }
    }
    
    /**
     * Build a new memory object
     */
    private fun buildMemory(
        communityId: String,
        title: String,
        isHorizontal: Boolean,
        photos: List<FromUser>
    ): Memory {
        return Memory(
            id = "memory_${UUID.randomUUID()}",
            communityId = communityId,
            title = title,
            isHorizontal = isHorizontal,
            photos = photos
        )
    }
}