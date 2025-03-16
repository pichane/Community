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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class GetDiscoveryCommunitiesUseCaseTest {

    @Mock
    private lateinit var socialRepository: SocialRepository

    private lateinit var getDiscoveryCommunitiesUseCase: GetDiscoveryCommunitiesUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getDiscoveryCommunitiesUseCase = GetDiscoveryCommunitiesUseCase(socialRepository)
    }

    @Test
    fun `invoke returns discovery communities from repository`() = runTest {
        // Arrange
        val mockMemory = Memory(
            id = "memory1",
            communityId = "discovery1",
            title = "Sample Memory",
            isHorizontal = true,
            photos = listOf(
                FromUser(id = "photo1", userId = "user1", url = "https://example.com/photo1")
            )
        )
        
        val expectedCommunities = listOf(
            Community(
                id = "discovery1",
                userName = "Nature Photography",
                profilePictureUrl = "https://example.com/community1",
                isCommunityMember = false,
                memories = listOf(mockMemory)
            ),
            Community(
                id = "discovery2",
                userName = "Urban Explorers",
                profilePictureUrl = "https://example.com/community2",
                isCommunityMember = false,
                memories = emptyList()
            )
        )

        `when`(socialRepository.getDiscoveryCommunities()).thenReturn(flow { emit(expectedCommunities) })

        // Act
        val result = getDiscoveryCommunitiesUseCase().first()

        // Assert
        assertEquals(expectedCommunities, result)
        assertEquals(2, result.size)
        assertEquals("discovery1", result[0].id)
        assertEquals("Nature Photography", result[0].userName)
        assertEquals(1, result[0].memories.size)
        
        assertEquals("discovery2", result[1].id)
        assertEquals("Urban Explorers", result[1].userName)
        assertEquals(0, result[1].memories.size)
        
        // Verify all communities are not joined yet
        assertTrue(result.none { it.isCommunityMember })
    }

    @Test
    fun `invoke returns empty list when repository returns empty`() = runTest {
        // Arrange
        val emptyList = emptyList<Community>()
        `when`(socialRepository.getDiscoveryCommunities()).thenReturn(flow { emit(emptyList) })

        // Act
        val result = getDiscoveryCommunitiesUseCase().first()

        // Assert
        assertEquals(emptyList, result)
        assertTrue(result.isEmpty())
    }
}