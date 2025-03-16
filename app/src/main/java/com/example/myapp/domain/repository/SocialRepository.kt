package com.example.myapp.domain.repository

import com.example.myapp.domain.model.Community
import com.example.myapp.domain.model.Memory
import com.example.myapp.domain.model.MissingPhotoInfo
import com.example.myapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface SocialRepository {
    fun getDiscoveryUsers(): Flow<List<User>>
    fun getUserCommunities(): Flow<List<Community>>  // Communities the user is already in
    fun getDiscoveryCommunities(): Flow<List<Community>>  // Communities to discover
    fun getFriends(): Flow<List<User>>
    suspend fun addFriend(userId: String)
    suspend fun joinCommunity(communityId: String)  // Add new function
    suspend fun updateMemoryPhoto(communityId: String, memoryId: String, photoId: String, newPhotoUrl: String)

    suspend fun addMemoryToCommunity(communityId: String, memory: Memory)
}