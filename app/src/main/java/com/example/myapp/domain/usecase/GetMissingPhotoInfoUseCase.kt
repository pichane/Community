package com.example.myapp.domain.usecase

import com.example.myapp.domain.model.MissingPhotoInfo
import com.example.myapp.domain.repository.SocialRepository
import kotlinx.coroutines.flow.first

class GetMissingPhotoInfoUseCase(private val socialRepository: SocialRepository) {

    suspend operator fun invoke(mainUserId: String = "user_main"): List<MissingPhotoInfo> {
        val communities = socialRepository.getUserCommunities().first()

        return communities.flatMap { community ->
            community.memories.mapNotNull { memory ->
                // Find the first photo in each memory that belongs to mainUser and has empty URL
                val missingUserPhoto = memory.photos.firstOrNull {
                    it.userId == mainUserId && it.url.isBlank()
                }

                if (missingUserPhoto != null) {
                    MissingPhotoInfo(
                        communityId = community.id,
                        communityName = community.userName,
                        memoryId = memory.id,
                        memoryTitle = memory.title,
                        photo = missingUserPhoto
                    )
                } else null
            }
        }
    }
}