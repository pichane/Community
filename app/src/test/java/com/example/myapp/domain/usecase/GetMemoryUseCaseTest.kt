package com.example.myapp.domain.usecase

import com.example.myapp.domain.model.Community
import com.example.myapp.domain.model.FromUser
import com.example.myapp.domain.model.Memory
import com.example.myapp.domain.repository.SocialRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class GetMemoryUseCaseTest {

    @Mock
    private lateinit var socialRepository: SocialRepository

    private lateinit var getMemoryUseCase: GetMemoryUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getMemoryUseCase = GetMemoryUseCase(socialRepository)
    }

    @Test
    fun `invoke returns memory when found in community`() = runTest {
        // Arrange
        val communityId = "community1"
        val memoryId = "memory1"
        
        val memory = Memory(
            id = memoryId,
            communityId = communityId,
            title = "Test Memory",
            isHorizontal = true,
            photos = listOf(
                FromUser(id = "photo1", userId = "user1", url = "https://example.com/photo1")
            )
        )
        
        val community = Community(
            id = communityId,
            userName = "Test Community",
            profilePictureUrl = "https://example.com/profile",
            isCommunityMember = true,
            memories = listOf(memory)
        )

        `when`(socialRepository.getUserCommunities()).thenReturn(
            flow { emit(listOf(community)) }
        )

        // Act
        val result = getMemoryUseCase(communityId, memoryId).memory

        // Assert
        assertEquals(memory, result)
    }

    @Test
    fun `invoke returns null when memory not found`() = runTest {
        // Arrange
        val communityId = "community1"
        val memoryId = "nonexistent_memory"
        
        val memory = Memory(
            id = "memory1",
            communityId = communityId,
            title = "Test Memory",
            isHorizontal = true,
            photos = listOf(
                FromUser(id = "photo1", userId = "user1", url = "https://example.com/photo1")
            )
        )
        
        val community = Community(
            id = communityId,
            userName = "Test Community",
            profilePictureUrl = "https://example.com/profile",
            isCommunityMember = true,
            memories = listOf(memory)
        )

        `when`(socialRepository.getUserCommunities()).thenReturn(
            flow { emit(listOf(community)) }
        )

        // Act
        val result = getMemoryUseCase(communityId, memoryId).memory

        // Assert
        assertNull(result)
    }
}