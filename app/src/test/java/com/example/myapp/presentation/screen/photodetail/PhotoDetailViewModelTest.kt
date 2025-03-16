package com.example.myapp.presentation.screen.photodetail

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.myapp.domain.model.Community
import com.example.myapp.domain.model.MissingPhotoInfo
import com.example.myapp.domain.model.Photo
import com.example.myapp.domain.model.User
import com.example.myapp.domain.usecase.AddFriendUseCase
import com.example.myapp.domain.usecase.AddMemoryToCommunityUseCase
import com.example.myapp.domain.usecase.DeletePhotoUseCase
import com.example.myapp.domain.usecase.GetDiscoveryCommunitiesUseCase
import com.example.myapp.domain.usecase.GetDiscoveryUsersUseCase
import com.example.myapp.domain.usecase.GetFriendsUseCase
import com.example.myapp.domain.usecase.GetMissingPhotoInfoUseCase
import com.example.myapp.domain.usecase.GetPhotoByIdUseCase
import com.example.myapp.domain.usecase.GetUserCommunitiesUseCase
import com.example.myapp.domain.usecase.JoinCommunityUseCase
import com.example.myapp.domain.usecase.UpdateMemoryPhotoUseCase
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
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class PhotoDetailViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var getPhotoByIdUseCase: GetPhotoByIdUseCase
    
    @Mock
    private lateinit var deletePhotoUseCase: DeletePhotoUseCase
    
    @Mock
    private lateinit var getMissingPhotoInfoUseCase: GetMissingPhotoInfoUseCase
    
    @Mock
    private lateinit var updateMemoryPhotoUseCase: UpdateMemoryPhotoUseCase
    
    @Mock
    private lateinit var getUserCommunitiesUseCase: GetUserCommunitiesUseCase
    
    @Mock
    private lateinit var addMemoryToCommunityUseCase: AddMemoryToCommunityUseCase

    private lateinit var viewModel: PhotoDetailViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        // Setup default flow responses
        runTest {
            doReturn(emptyList<MissingPhotoInfo>()).`when`(getMissingPhotoInfoUseCase).invoke(anyString())
        }
        `when`(getUserCommunitiesUseCase()).thenReturn(flow { emit(emptyList()) })
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        // Act
        viewModel = PhotoDetailViewModel(
            getPhotoByIdUseCase,
            deletePhotoUseCase,
            getMissingPhotoInfoUseCase,
            updateMemoryPhotoUseCase,
            getUserCommunitiesUseCase,
            addMemoryToCommunityUseCase
        )
        
        // Assert
        val initialState = viewModel.uiState.value
        assertNull(initialState.photo)
        assertEquals(emptyList<MissingPhotoInfo>(), initialState.missingPhotoInfo)
        assertEquals(emptyList<Community>(), initialState.userCommunities)
        assertFalse(initialState.showCommunitySelection)
    }

    @Test
    fun `load photo updates state correctly`() = runTest {
        // Arrange
        val photoId = 123
        val photo = Photo(
            id = photoId,
            uri = Uri.parse("content://test/photo1"),
            timestamp = 1000L,
            title = "Test Photo"
        )
        
        `when`(getPhotoByIdUseCase(photoId)).thenReturn(photo)
        
        viewModel = PhotoDetailViewModel(
            getPhotoByIdUseCase,
            deletePhotoUseCase,
            getMissingPhotoInfoUseCase,
            updateMemoryPhotoUseCase,
            getUserCommunitiesUseCase,
            addMemoryToCommunityUseCase
        )
        
        // Act
        viewModel.onEvent(PhotoDetailEvent.LoadPhoto(photoId))
        
        // Assert
        val state = viewModel.uiState.value
        assertEquals(photo, state.photo)
        assertFalse(state.isLoading)
    }
    
    @Test
    fun `delete photo calls use case and updates navigation`() = runTest {
        // Arrange
        val photoId = 123
        val photo = Photo(photoId, Uri.parse("content://test/photo1"), 1000L, "Test Photo")
        
        `when`(getPhotoByIdUseCase(photoId)).thenReturn(photo)
        
        viewModel = PhotoDetailViewModel(
            getPhotoByIdUseCase,
            deletePhotoUseCase,
            getMissingPhotoInfoUseCase,
            updateMemoryPhotoUseCase,
            getUserCommunitiesUseCase,
            addMemoryToCommunityUseCase
        )
        
        viewModel.onEvent(PhotoDetailEvent.LoadPhoto(photoId))
        
        // Act
        viewModel.onEvent(PhotoDetailEvent.DeletePhoto(photoId))
        
        // Assert
        verify(deletePhotoUseCase).invoke(photoId)
    }
    
    @Test
    fun `show community selector updates state`() = runTest {
        // Arrange
        viewModel = PhotoDetailViewModel(
            getPhotoByIdUseCase,
            deletePhotoUseCase,
            getMissingPhotoInfoUseCase,
            updateMemoryPhotoUseCase,
            getUserCommunitiesUseCase,
            addMemoryToCommunityUseCase
        )
        
        // Act
        viewModel.onEvent(PhotoDetailEvent.ShowCommunitySelector)
        
        // Assert
        assertTrue(viewModel.uiState.value.showCommunitySelection)
    }
    
    @Test
    fun `dismiss community selector updates state`() = runTest {
        // Arrange
        viewModel = PhotoDetailViewModel(
            getPhotoByIdUseCase,
            deletePhotoUseCase,
            getMissingPhotoInfoUseCase,
            updateMemoryPhotoUseCase,
            getUserCommunitiesUseCase,
            addMemoryToCommunityUseCase
        )
        
        // First show the selector
        viewModel.onEvent(PhotoDetailEvent.ShowCommunitySelector)
        assertTrue(viewModel.uiState.value.showCommunitySelection)
        
        // Act
        viewModel.onEvent(PhotoDetailEvent.DismissCommunitySelector)
        
        // Assert
        assertFalse(viewModel.uiState.value.showCommunitySelection)
    }
    
    @Test
    fun `create event calls add memory use case`() = runTest {
        // Arrange
        val communityId = "community1"
        val photoUrl = "content://test/photo1"
        
        viewModel = PhotoDetailViewModel(
            getPhotoByIdUseCase,
            deletePhotoUseCase,
            getMissingPhotoInfoUseCase,
            updateMemoryPhotoUseCase,
            getUserCommunitiesUseCase,
            addMemoryToCommunityUseCase
        )
        
        // Act
        viewModel.onEvent(PhotoDetailEvent.CreateEvent(communityId, photoUrl))
        
        // Assert
        assertFalse(viewModel.uiState.value.showCommunitySelection)
    }
}