package com.example.myapp.domain.usecase

import com.example.myapp.domain.model.Community
import com.example.myapp.domain.model.FromUser
import com.example.myapp.domain.model.Memory
import com.example.myapp.domain.repository.SocialRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class GetUserCommunitiesUseCaseTest {

    @Mock
    private lateinit var socialRepository: SocialRepository

    private lateinit var getUserCommunitiesUseCase: GetUserCommunitiesUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getUserCommunitiesUseCase = GetUserCommunitiesUseCase(socialRepository)
    }

    @Test
    fun `invoke returns communities from repository`() = runTest {
        // Arrange
        val mockMemory = Memory(
            id = "memory1",
            communityId = "community1",
            title = "Test Memory",
            isHorizontal = true,
            photos = listOf(
                FromUser(id = "photo1", userId = "user1", url = "https://example.com/photo1"),
                FromUser(id = "photo2", userId = "user2", url = "https://example.com/photo2")
            )
        )
        
        val expectedCommunities = listOf(
            Community(
                id = "community1",
                userName = "Photography Club",
                profilePictureUrl = "https://example.com/profile1",
                isCommunityMember = true,
                memories = listOf(mockMemory)
            ),
            Community(
                id = "community2",
                userName = "Hiking Group",
                profilePictureUrl = "https://example.com/profile2",
                isCommunityMember = true,
                memories = emptyList()
            )
        )

        `when`(socialRepository.getUserCommunities()).thenReturn(flow { emit(expectedCommunities) })

        // Act
        val result = getUserCommunitiesUseCase().first()

        // Assert
        assertEquals(expectedCommunities, result)
        assertEquals(2, result.size)
        assertEquals("community1", result[0].id)
        assertEquals("Photography Club", result[0].userName)
        assertEquals(1, result[0].memories.size)
        assertEquals("community2", result[1].id)
        assertEquals("Hiking Group", result[1].userName)
        assertEquals(0, result[1].memories.size)
    }

    @Test
    fun `invoke returns empty list when repository returns empty`() = runTest {
        // Arrange
        val emptyList = emptyList<Community>()
        `when`(socialRepository.getUserCommunities()).thenReturn(flow { emit(emptyList) })

        // Act
        val result = getUserCommunitiesUseCase().first()

        // Assert
        assertEquals(emptyList, result)
    }
}