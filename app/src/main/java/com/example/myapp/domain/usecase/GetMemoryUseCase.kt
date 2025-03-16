package com.example.myapp.domain.usecase

import com.example.myapp.domain.model.Memory
import com.example.myapp.domain.repository.SocialRepository
import kotlinx.coroutines.flow.firstOrNull


class GetMemoryUseCase(private val socialRepository: SocialRepository) {

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

data class MemoryResult(
    val memory: Memory?,
    val communityName: String?
)