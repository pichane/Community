package com.example.myapp.domain.usecase

import com.example.myapp.domain.repository.SocialRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.doThrow
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class JoinCommunityUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var socialRepository: SocialRepository

    private lateinit var joinCommunityUseCase: JoinCommunityUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        joinCommunityUseCase = JoinCommunityUseCase(socialRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke returns true on successful community join`() = runTest {
        // Arrange
        val communityId = "community1"
        doAnswer { /* do nothing, succeed */ }.`when`(socialRepository).joinCommunity(communityId)

        // Act
        val result = joinCommunityUseCase(communityId)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `invoke returns false when exception occurs`() = runTest {
        // Arrange
        val communityId = "community1"
        doThrow(RuntimeException("Network error")).`when`(socialRepository).joinCommunity(communityId)

        // Act
        val result = joinCommunityUseCase(communityId)

        // Assert
        assertFalse(result)
    }
}