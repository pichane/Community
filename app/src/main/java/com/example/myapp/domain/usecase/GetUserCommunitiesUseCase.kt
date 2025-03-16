package com.example.myapp.domain.usecase

import com.example.myapp.domain.model.Community
import com.example.myapp.domain.repository.SocialRepository
import kotlinx.coroutines.flow.Flow


class GetUserCommunitiesUseCase(private val socialRepository: SocialRepository) {
    operator fun invoke(): Flow<List<Community>> {
        return socialRepository.getUserCommunities()
    }
}
