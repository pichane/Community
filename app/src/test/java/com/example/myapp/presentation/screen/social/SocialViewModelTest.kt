package com.example.myapp.presentation.screen.social

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.myapp.domain.model.Community
import com.example.myapp.domain.model.User
import com.example.myapp.domain.usecase.AddFriendUseCase
import com.example.myapp.domain.usecase.GetDiscoveryCommunitiesUseCase
import com.example.myapp.domain.usecase.GetDiscoveryUsersUseCase
import com.example.myapp.domain.usecase.GetFriendsUseCase
import com.example.myapp.domain.usecase.GetUserCommunitiesUseCase
import com.example.myapp.domain.usecase.JoinCommunityUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class SocialViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var getDiscoveryUsersUseCase: GetDiscoveryUsersUseCase

    @Mock
    private lateinit var getUserCommunitiesUseCase: GetUserCommunitiesUseCase

    @Mock
    private lateinit var getDiscoveryCommunitiesUseCase: GetDiscoveryCommunitiesUseCase

    @Mock
    private lateinit var getFriendsUseCase: GetFriendsUseCase

    @Mock
    private lateinit var addFriendUseCase: AddFriendUseCase

    @Mock
    private lateinit var joinCommunityUseCase: JoinCommunityUseCase

    private lateinit var socialViewModel: SocialViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        // Setup default responses for flows
        `when`(getDiscoveryUsersUseCase()).thenReturn(flow { emit(emptyList()) })
        `when`(getUserCommunitiesUseCase()).thenReturn(flow { emit(emptyList()) })
        `when`(getDiscoveryCommunitiesUseCase()).thenReturn(flow { emit(emptyList()) })
        `when`(getFriendsUseCase()).thenReturn(flow { emit(emptyList()) })
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `add friend updates state correctly`() = runTest {
        // Arrange
        val userId = "user123"
        `when`(addFriendUseCase(userId)).thenReturn(true)

        socialViewModel = SocialViewModel(
            getDiscoveryUsersUseCase,
            getUserCommunitiesUseCase,
            getDiscoveryCommunitiesUseCase,
            getFriendsUseCase,
            addFriendUseCase,
            joinCommunityUseCase
        )

        // Act
        socialViewModel.onEvent(SocialEvent.AddFriend(userId))

        // Assert
        verify(addFriendUseCase).invoke(userId)
    }

    @Test
    fun `join community updates state correctly`() = runTest {
        // Arrange
        val communityId = "community123"
        `when`(joinCommunityUseCase(communityId)).thenReturn(true)

        socialViewModel = SocialViewModel(
            getDiscoveryUsersUseCase,
            getUserCommunitiesUseCase,
            getDiscoveryCommunitiesUseCase,
            getFriendsUseCase,
            addFriendUseCase,
            joinCommunityUseCase
        )

        // Act
        socialViewModel.onEvent(SocialEvent.JoinCommunity(communityId))

        // Assert
        verify(joinCommunityUseCase).invoke(communityId)
    }

    @Test
    fun `error in loading updates error state`() = runTest {
        // Arrange
        val errorMessage = "Failed to load users"
        val errorFlow = flow<List<User>> { throw Exception(errorMessage) }

        `when`(getDiscoveryUsersUseCase()).thenReturn(errorFlow)

        // Act
        socialViewModel = SocialViewModel(
            getDiscoveryUsersUseCase,
            getUserCommunitiesUseCase,
            getDiscoveryCommunitiesUseCase,
            getFriendsUseCase,
            addFriendUseCase,
            joinCommunityUseCase
        )

        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = socialViewModel.uiState.value
        assertEquals(errorMessage, state.error)
        assertFalse(state.isLoading)
    }
}