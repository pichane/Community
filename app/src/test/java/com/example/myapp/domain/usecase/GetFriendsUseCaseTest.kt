package com.example.myapp.domain.usecase

import com.example.myapp.domain.model.User
import com.example.myapp.domain.repository.SocialRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
class GetFriendsUseCaseTest {

    @Mock
    private lateinit var socialRepository: SocialRepository

    private lateinit var getFriendsUseCase: GetFriendsUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getFriendsUseCase = GetFriendsUseCase(socialRepository)
    }

    @Test
    fun `invoke returns friends from repository`() = runTest {
        // Arrange
        val expectedFriends = listOf(
            User(id = "friend1", userName = "Friend 1", profilePictureUrl = "https://example.com/friend1.jpg", isFriend = true),
            User(id = "friend2", userName = "Friend 2", profilePictureUrl = "https://example.com/friend2.jpg", isFriend = true)
        )

        `when`(socialRepository.getFriends()).thenReturn(flow { emit(expectedFriends) })

        // Act
        val result = getFriendsUseCase().first()

        // Assert
        assertEquals(expectedFriends, result)
        assertTrue(result.all { it.isFriend })
    }
}