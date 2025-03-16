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
class AddFriendUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var socialRepository: SocialRepository

    private lateinit var addFriendUseCase: AddFriendUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        addFriendUseCase = AddFriendUseCase(socialRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke returns true on successful friend addition`() = runTest {
        // Arrange
        val userId = "user1"
        doAnswer { /* do nothing, succeed */ }.`when`(socialRepository).addFriend(userId)

        // Act
        val result = addFriendUseCase(userId)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `invoke returns false when exception occurs`() = runTest {
        // Arrange
        val userId = "user1"
        doThrow(RuntimeException("Network error")).`when`(socialRepository).addFriend(userId)

        // Act
        val result = addFriendUseCase(userId)

        // Assert
        assertFalse(result)
    }
}