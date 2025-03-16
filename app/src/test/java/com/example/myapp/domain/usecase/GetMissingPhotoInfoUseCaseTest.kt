package com.example.myapp.domain.usecase

import com.example.myapp.domain.model.Community
import com.example.myapp.domain.model.FromUser
import com.example.myapp.domain.model.Memory
import com.example.myapp.domain.model.MissingPhotoInfo
import com.example.myapp.domain.repository.SocialRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class GetMissingPhotoInfoUseCaseTest {

    @Mock
    private lateinit var socialRepository: SocialRepository

    private lateinit var getMissingPhotoInfoUseCase: GetMissingPhotoInfoUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getMissingPhotoInfoUseCase = GetMissingPhotoInfoUseCase(socialRepository)
    }

    @Test
    fun `invoke returns missing photo info for main user`() = runTest {
        // Arrange
        val mainUserId = "user_main"
        
        val memory1 = Memory(
            id = "memory1",
            communityId = "community1",
            title = "Test Memory 1",
            isHorizontal = true,
            photos = listOf(
                FromUser(id = "photo1", userId = "user1", url = "https://example.com/photo1"),
                FromUser(id = "photo2", userId = mainUserId, url = "") // Missing photo
            )
        )
        
        val memory2 = Memory(
            id = "memory2",
            communityId = "community1",
            title = "Test Memory 2",
            isHorizontal = false,
            photos = listOf(
                FromUser(id = "photo3", userId = mainUserId, url = "https://example.com/photo3"),
                FromUser(id = "photo4", userId = "user2", url = "")
            )
        )
        
        val community = Community(
            id = "community1",
            userName = "Test Community",
            profilePictureUrl = "https://example.com/profile",
            isCommunityMember = true,
            memories = listOf(memory1, memory2)
        )

        `when`(socialRepository.getUserCommunities()).thenReturn(
            flow { emit(listOf(community)) }
        )

        // Act
        val result = getMissingPhotoInfoUseCase(mainUserId)

        // Assert
        assertEquals(1, result.size)
        assertEquals("memory1", result[0].memoryId)
        assertEquals("community1", result[0].communityId)
        // assertEquals("photo2", result[0].photoId)
    }

    @Test
    fun `invoke returns empty list when no missing photos`() = runTest {
        // Arrange
        val mainUserId = "user_main"
        
        val memory = Memory(
            id = "memory1",
            communityId = "community1",
            title = "Test Memory",
            isHorizontal = true,
            photos = listOf(
                FromUser(id = "photo1", userId = "user1", url = "https://example.com/photo1"),
                FromUser(id = "photo2", userId = mainUserId, url = "https://example.com/photo2") // Not missing
            )
        )
        
        val community = Community(
            id = "community1",
            userName = "Test Community",
            profilePictureUrl = "https://example.com/profile",
            isCommunityMember = true,
            memories = listOf(memory)
        )

        `when`(socialRepository.getUserCommunities()).thenReturn(
            flow { emit(listOf(community)) }
        )

        // Act
        val result = getMissingPhotoInfoUseCase(mainUserId)

        // Assert
        assertTrue(result.isEmpty())
    }
}