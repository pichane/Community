package com.example.myapp.data.repository

import com.example.myapp.domain.model.FromUser
import com.example.myapp.domain.model.Memory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class SocialRepositoryImplTest {

    private lateinit var socialRepository: SocialRepositoryImpl

    @Before
    fun setup() {
        socialRepository = SocialRepositoryImpl()
    }

    @Test
    fun `getUserCommunities returns communities`() = runTest {
        // Act
        val communities = socialRepository.getUserCommunities().first()

        // Assert
        assertTrue(communities.isNotEmpty())
        assertTrue(communities.all { it.isCommunityMember })
    }

    @Test
    fun `getDiscoveryUsers returns users`() = runTest {
        // Act
        val users = socialRepository.getDiscoveryUsers().first()

        // Assert
        assertTrue(users.isNotEmpty())
    }

    @Test
    fun `getDiscoveryCommunities returns communities`() = runTest {
        // Act
        val communities = socialRepository.getDiscoveryCommunities().first()

        // Assert
        assertTrue(communities.isNotEmpty())
    }

    @Test
    fun `getFriends returns friends`() = runTest {
        // Act
        val friends = socialRepository.getFriends().first()

        // Assert
        assertTrue(friends.isNotEmpty())
        assertTrue(friends.all { it.isFriend })
    }

    @Test
    fun `addFriend moves user from discovery to friends`() = runTest {
        // Arrange
        val initialDiscoveryUsers = socialRepository.getDiscoveryUsers().first()
        val initialFriends = socialRepository.getFriends().first()
        
        // Choose a user from discovery
        val userToAdd = initialDiscoveryUsers.firstOrNull()
        val userId = userToAdd!!.id

        // Act
        socialRepository.addFriend(userId)
        
        // Assert
        val updatedDiscoveryUsers = socialRepository.getDiscoveryUsers().first()
        val updatedFriends = socialRepository.getFriends().first()
        
        // User should be removed from discovery
        assertFalse(updatedDiscoveryUsers.any { it.id == userId })
        
        // User should be added to friends
        assertTrue(updatedFriends.any { it.id == userId })
        
        // Total count should remain the same
        assertEquals(
            initialDiscoveryUsers.size + initialFriends.size,
            updatedDiscoveryUsers.size + updatedFriends.size
        )
    }

    @Test
    fun `joinCommunity moves community from discovery to user communities`() = runTest {
        // Arrange
        val initialDiscoveryCommunities = socialRepository.getDiscoveryCommunities().first()
        val initialUserCommunities = socialRepository.getUserCommunities().first()
        
        // Choose a community from discovery
        val communityToJoin = initialDiscoveryCommunities.firstOrNull()
        val communityId = communityToJoin!!.id

        // Act
        socialRepository.joinCommunity(communityId)
        
        // Assert
        val updatedDiscoveryCommunities = socialRepository.getDiscoveryCommunities().first()
        val updatedUserCommunities = socialRepository.getUserCommunities().first()
        
        // Community should be removed from discovery
        assertFalse(updatedDiscoveryCommunities.any { it.id == communityId })
        
        // Community should be added to user communities
        assertTrue(updatedUserCommunities.any { it.id == communityId })
        
        // The joined community should be marked as a member
        val joinedCommunity = updatedUserCommunities.find { it.id == communityId }
        assertNotNull(joinedCommunity)
        assertTrue(joinedCommunity!!.isCommunityMember)
        
        // Total count should remain the same
        assertEquals(
            initialDiscoveryCommunities.size + initialUserCommunities.size,
            updatedDiscoveryCommunities.size + updatedUserCommunities.size
        )
    }

    @Test
    fun `updateMemoryPhoto updates photo url`() = runTest {
        // Arrange
        val communityId = "community1" // First community
        val memoryId = "memory1" // First memory in first community
        val photoId = "photo3" // Photo with empty URL
        val newPhotoUrl = "https://example.com/new_photo.jpg"
        
        val initialCommunities = socialRepository.getUserCommunities().first()
        val initialCommunity = initialCommunities.find { it.id == communityId }
        val initialMemory = initialCommunity!!.memories.find { it.id == memoryId }
        val initialPhoto = initialMemory!!.photos.find { it.id == photoId }
        
        // Verify initial state (URL should be empty)
        assertTrue(initialPhoto!!.url.isEmpty())

        // Act
        socialRepository.updateMemoryPhoto(communityId, memoryId, photoId, newPhotoUrl)
        
        // Assert
        val updatedCommunities = socialRepository.getUserCommunities().first()
        val updatedCommunity = updatedCommunities.find { it.id == communityId }
        val updatedMemory = updatedCommunity?.memories?.find { it.id == memoryId }
        val updatedPhoto = updatedMemory?.photos?.find { it.id == photoId }
        
        assertNotNull(updatedPhoto)
        assertEquals(newPhotoUrl, updatedPhoto!!.url)
    }

    @Test
    fun `addMemoryToCommunity adds new memory`() = runTest {
        // Arrange
        val communityId = "community1"
        val initialCommunities = socialRepository.getUserCommunities().first()
        val initialCommunity = initialCommunities.find { it.id == communityId }
        val initialMemoriesCount = initialCommunity!!.memories.size
        
        val newMemory = Memory(
            id = "new_memory_test",
            communityId = communityId,
            title = "Test New Memory",
            isHorizontal = true,
            photos = listOf(
                FromUser(id = "new_photo1", userId = "user_main", url = "https://example.com/photo1"),
                FromUser(id = "new_photo2", userId = "friend1", url = "https://example.com/photo2")
            )
        )

        // Act
        socialRepository.addMemoryToCommunity(communityId, newMemory)
        
        // Assert
        val updatedCommunities = socialRepository.getUserCommunities().first()
        val updatedCommunity = updatedCommunities.find { it.id == communityId }
        
        assertNotNull(updatedCommunity)
        assertEquals(initialMemoriesCount + 1, updatedCommunity!!.memories.size)
        
        // Check the new memory was added
        val addedMemory = updatedCommunity.memories.find { it.id == newMemory.id }
        assertNotNull(addedMemory)
        assertEquals(newMemory.title, addedMemory!!.title)
        assertEquals(newMemory.isHorizontal, addedMemory.isHorizontal)
        assertEquals(newMemory.photos.size, addedMemory.photos.size)
    }
}