package com.example.myapp.domain.usecase

import com.example.myapp.domain.model.Community
import com.example.myapp.domain.model.User
import com.example.myapp.domain.repository.SocialRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GetDiscoveryUsersUseCase(
    private val socialRepository: SocialRepository
) {
    // Returns a flow of users that can be discovered by the current user
    operator fun invoke(): Flow<List<User>> {
        return socialRepository.getDiscoveryUsers()
    }
}

class GetFriendsUseCase(
    private val socialRepository: SocialRepository
) {
    // Returns a flow of the current user's friends
    operator fun invoke(): Flow<List<User>> {
        return socialRepository.getFriends()
    }
}

class GetDiscoveryCommunitiesUseCase(
    private val socialRepository: SocialRepository
) {
    // Returns a flow of communities that the user can join
    operator fun invoke(): Flow<List<Community>> {
        return socialRepository.getDiscoveryCommunities()
    }
}

class AddFriendUseCase(
    private val socialRepository: SocialRepository
) {
    // Adds a friend by user ID and returns success status
    suspend operator fun invoke(userId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                socialRepository.addFriend(userId)
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}

class JoinCommunityUseCase(
    private val socialRepository: SocialRepository
) {
    // Joins a community by ID and returns success status
    suspend operator fun invoke(communityId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                socialRepository.joinCommunity(communityId)
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}
