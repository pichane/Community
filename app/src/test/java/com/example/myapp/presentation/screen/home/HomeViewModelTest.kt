package com.example.myapp.presentation.screen.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.myapp.domain.model.Community
import com.example.myapp.domain.model.FromUser
import com.example.myapp.domain.model.Memory
import com.example.myapp.domain.model.Photo
import com.example.myapp.domain.usecase.GetAllPhotosUseCase
import com.example.myapp.domain.usecase.GetUserCommunitiesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import android.net.Uri
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var getAllPhotosUseCase: GetAllPhotosUseCase

    @Mock
    private lateinit var getUserCommunitiesUseCase: GetUserCommunitiesUseCase

    private lateinit var homeViewModel: HomeViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        // Arrange
        val photosFlow = flow<List<Photo>> { emit(emptyList()) }
        val communitiesFlow = flow<List<Community>> { emit(emptyList()) }
        
        `when`(getAllPhotosUseCase()).thenReturn(photosFlow)
        `when`(getUserCommunitiesUseCase()).thenReturn(communitiesFlow)
        
        // Act
        homeViewModel = HomeViewModel(getAllPhotosUseCase, getUserCommunitiesUseCase)
        
        // Assert
        val initialState = homeViewModel.uiState.value
        assertFalse(initialState.isLoading)
        assertEquals(emptyList<Photo>(), initialState.photos)
        assertEquals(emptyList<Community>(), initialState.communities)
        assertNull(initialState.error)
    }

    @Test
    fun `loading data updates state correctly`() = runTest {
        // Arrange
        val mockPhotos = listOf(
            Photo(1, Uri.parse("content://test/photo1"), 1000L, "Photo 1"),
            Photo(2, Uri.parse("content://test/photo2"), 2000L, "Photo 2")
        )
        
        val mockMemory = Memory(
            id = "memory1",
            communityId = "community1",
            title = "Test Memory",
            isHorizontal = true,
            photos = listOf(
                FromUser(id = "photo1", userId = "user1", url = "https://example.com/photo1")
            )
        )
        
        val mockCommunities = listOf(
            Community(
                id = "community1",
                userName = "Test Community",
                profilePictureUrl = "https://example.com/profile",
                isCommunityMember = true,
                memories = listOf(mockMemory)
            )
        )
        
        val photosFlow = flow { emit(mockPhotos) }
        val communitiesFlow = flow { emit(mockCommunities) }
        
        `when`(getAllPhotosUseCase()).thenReturn(photosFlow)
        `when`(getUserCommunitiesUseCase()).thenReturn(communitiesFlow)
        
        // Act
        homeViewModel = HomeViewModel(getAllPhotosUseCase, getUserCommunitiesUseCase)
        
        // Assert
        val state = homeViewModel.uiState.value
        assertEquals(mockPhotos, state.photos)
        assertEquals(mockCommunities, state.communities)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `error in photos flow updates error state`() = runTest {
        // Arrange
        val errorMessage = "Failed to load photos"
        val photosFlow = flow<List<Photo>> { throw Exception(errorMessage) }
        val communitiesFlow = flow<List<Community>> { emit(emptyList()) }
        
        `when`(getAllPhotosUseCase()).thenReturn(photosFlow)
        `when`(getUserCommunitiesUseCase()).thenReturn(communitiesFlow)
        
        // Act
        homeViewModel = HomeViewModel(getAllPhotosUseCase, getUserCommunitiesUseCase)
        
        // Assert
        val state = homeViewModel.uiState.value
        assertEquals(errorMessage, state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun `refresh data reloads both photos and communities`() = runTest {
        // Arrange
        val initialPhotos = listOf(Photo(1, Uri.parse("content://test/photo1"), 1000L, "Photo 1"))
        val updatedPhotos = listOf(
            Photo(1, Uri.parse("content://test/photo1"), 1000L, "Photo 1"),
            Photo(2, Uri.parse("content://test/photo2"), 2000L, "Photo 2")
        )
        
        val initialPhotosFlow = MutableStateFlow(initialPhotos)
        val initialCommunitiesFlow = MutableStateFlow(emptyList<Community>())
        
        `when`(getAllPhotosUseCase()).thenReturn(initialPhotosFlow)
        `when`(getUserCommunitiesUseCase()).thenReturn(initialCommunitiesFlow)
        
        homeViewModel = HomeViewModel(getAllPhotosUseCase, getUserCommunitiesUseCase)
        
        // Initial state check
        assertEquals(initialPhotos, homeViewModel.uiState.value.photos)
        
        // Update the flows that will be returned on refresh
        initialPhotosFlow.value = updatedPhotos
        
        // Act - refresh data
        homeViewModel.onEvent(HomeEvent.RefreshData)
        
        // Assert
        val refreshedState = homeViewModel.uiState.value
        assertEquals(updatedPhotos, refreshedState.photos)
    }

    @Test
    fun `error shown event clears error state`() = runTest {
        // Arrange
        val errorMessage = "Test error"
        val photosFlow = flow<List<Photo>> { throw Exception(errorMessage) }
        val communitiesFlow = flow<List<Community>> { emit(emptyList()) }
        
        `when`(getAllPhotosUseCase()).thenReturn(photosFlow)
        `when`(getUserCommunitiesUseCase()).thenReturn(communitiesFlow)
        
        homeViewModel = HomeViewModel(getAllPhotosUseCase, getUserCommunitiesUseCase)
        
        // Verify error is set
        assertEquals(errorMessage, homeViewModel.uiState.value.error)
        
        // Act - mark error as shown
        homeViewModel.onEvent(HomeEvent.ErrorShown)
        
        // Assert
        assertNull(homeViewModel.uiState.value.error)
    }
}