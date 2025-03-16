package com.example.myapp.domain.usecase

import com.example.myapp.domain.model.User
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
class GetDiscoveryUsersUseCaseTest {

    @Mock
    private lateinit var socialRepository: SocialRepository

    private lateinit var getDiscoveryUsersUseCase: GetDiscoveryUsersUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getDiscoveryUsersUseCase = GetDiscoveryUsersUseCase(socialRepository)
    }

    @Test
    fun `invoke returns discovery users from repository`() = runTest {
        // Arrange
        val expectedUsers = listOf(
            User(id = "user1", userName = "User 1", profilePictureUrl = "https://example.com/user1.jpg", isFriend = false),
            User(id = "user2", userName = "User 2", profilePictureUrl = "https://example.com/user2.jpg", isFriend = false)
        )

        `when`(socialRepository.getDiscoveryUsers()).thenReturn(flow { emit(expectedUsers) })

        // Act
        val result = getDiscoveryUsersUseCase().first()

        // Assert
        assertEquals(expectedUsers, result)
    }
}