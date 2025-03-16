package com.example.myapp.domain.usecase

import com.example.myapp.domain.model.Memory
import com.example.myapp.domain.repository.SocialRepository
import kotlinx.coroutines.flow.firstOrNull

/**
 * Use case responsible for retrieving a specific memory from a community
 */
class GetMemoryUseCase(private val socialRepository: SocialRepository) {

    /**
     * Retrieves a memory and its associated community name
     * 
     * @param communityId The ID of the community
     * @param memoryId The ID of the memory
     * @return A [MemoryResult] containing the memory and community name
     */
    suspend operator fun invoke(communityId: String, memoryId: String): MemoryResult {
        val communities = socialRepository.getUserCommunities().firstOrNull() ?: return MemoryResult(null, null)
        
        val community = communities.find { it.id == communityId }
        val memory = community?.memories?.find { it.id == memoryId }
        
        return MemoryResult(
            memory = memory,
            communityName = community?.userName
        )
    }
}

/**
 * Data class to hold the result of a memory fetch operation
 */
data class MemoryResult(
    val memory: Memory?,
    val communityName: String?
)