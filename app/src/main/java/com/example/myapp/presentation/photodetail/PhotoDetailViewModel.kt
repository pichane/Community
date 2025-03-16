package com.example.myapp.presentation.photodetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.domain.model.Community
import com.example.myapp.domain.model.MissingPhotoInfo
import com.example.myapp.domain.model.Photo
import com.example.myapp.domain.usecase.AddMemoryToCommunityUseCase
import com.example.myapp.domain.usecase.DeletePhotoUseCase
import com.example.myapp.domain.usecase.GetMissingPhotoInfoUseCase
import com.example.myapp.domain.usecase.GetPhotoByIdUseCase
import com.example.myapp.domain.usecase.GetUserCommunitiesUseCase
import com.example.myapp.domain.usecase.UpdateMemoryPhotoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class PhotoDetailViewModel(
    private val getPhotoByIdUseCase: GetPhotoByIdUseCase,
    private val deletePhotoUseCase: DeletePhotoUseCase,
    private val getMissingPhotoInfoUseCase: GetMissingPhotoInfoUseCase,
    private val updateMemoryPhotoUseCase: UpdateMemoryPhotoUseCase,
    private val getUserCommunitiesUseCase: GetUserCommunitiesUseCase,
    private val addMemoryToCommunityUseCase: AddMemoryToCommunityUseCase
) : ViewModel() {

    // Unified UI state
    private val _uiState = MutableStateFlow(PhotoDetailUiState())
    val uiState: StateFlow<PhotoDetailUiState> = _uiState.asStateFlow()

    // Keep communities as a separate flow from domain layer
    private val userCommunitiesFlow =
        getUserCommunitiesUseCase().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        loadMissingPhotoInfo()
        observeCommunities()
    }

    fun onEvent(event: PhotoDetailEvent) {
        when (event) {
            is PhotoDetailEvent.LoadPhoto -> loadPhoto(event.photoId)
            is PhotoDetailEvent.DeletePhoto -> deletePhoto(event.photoId)
            is PhotoDetailEvent.UpdateMissingPhoto -> updateMissingPhotoWithCurrentPhoto(
                event.missingPhotoInfo, event.currentPhotoUri
            )

            is PhotoDetailEvent.UpdateAllMissingPhotos -> updateAllMissingPhotos(event.currentPhotoUri)
            is PhotoDetailEvent.CreateEvent -> createNewEventWithPhoto(
                event.communityId, event.photoUri
            )

            PhotoDetailEvent.ShowCommunitySelector -> showCommunitySelector()
            PhotoDetailEvent.DismissCommunitySelector -> dismissCommunitySelector()
            PhotoDetailEvent.NavigationHandled -> clearNavigationEvent()
        }
    }

    private fun loadPhoto(photoId: Int) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val photo = getPhotoByIdUseCase(photoId)
                _uiState.update { it.copy(photo = photo, isLoading = false) }
            } catch (e: Exception) {
                handleError(e, "Error loading photo")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun loadMissingPhotoInfo() {
        viewModelScope.launch {
            try {
                val missingInfo = getMissingPhotoInfoUseCase()
                _uiState.update { it.copy(missingPhotoInfo = missingInfo) }
            } catch (e: Exception) {
                handleError(e, "Error loading missing photo info")
            }
        }
    }

    private fun observeCommunities() {
        viewModelScope.launch {
            userCommunitiesFlow.collect { communities ->
                _uiState.update { it.copy(userCommunities = communities) }
            }
        }
    }

    private fun deletePhoto(photoId: Int) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                deletePhotoUseCase(photoId)

                _uiState.update {
                    it.copy(
                        isLoading = false, navigationEvent = NavigationEvent.PhotoDeleted
                    )
                }
            } catch (e: Exception) {
                handleError(e, "Error deleting photo")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun updateMissingPhotoWithCurrentPhoto(
        missingPhotoInfo: MissingPhotoInfo, currentPhotoUri: String
    ) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                // Only proceed if we have a valid photo reference
                val photoId = missingPhotoInfo.photo?.id ?: run {
                    Log.w("PhotoDetailViewModel", "Missing photo ID")
                    return@launch
                }

                // Update the memory photo with current photo URI
                updateMemoryPhotoUseCase(
                    communityId = missingPhotoInfo.communityId,
                    memoryId = missingPhotoInfo.memoryId,
                    photoId = photoId,
                    newPhotoUrl = currentPhotoUri
                )

                // Refresh the missing photo info list
                val updatedMissingInfo = getMissingPhotoInfoUseCase()

                _uiState.update {
                    it.copy(
                        missingPhotoInfo = updatedMissingInfo,
                        isLoading = false,
                        navigationEvent = NavigationEvent.NavigateBack
                    )
                }
            } catch (e: Exception) {
                handleError(e, "Error updating missing photo")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun createNewEventWithPhoto(communityId: String, photoUri: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, showCommunitySelection = false) }

                // Create event title based on date
                val dateFormat = SimpleDateFormat("MMMM dd", Locale.getDefault())
                val title = "Event on ${dateFormat.format(Date())}"

                // Create a new memory in the community
                addMemoryToCommunityUseCase(
                    communityId = communityId, title = title, date = Date()
                )

                // Get the community with the new memory
                val community = uiState.value.userCommunities.find { it.id == communityId } ?: run {
                    Log.w("PhotoDetailViewModel", "Community not found")
                    return@launch
                }

                if (community.memories.isEmpty()) {
                    Log.w("PhotoDetailViewModel", "No memories found in community")
                    return@launch
                }

                // Get the newest memory (should be first in the list)
                val newestMemory = community.memories.first()

                // Find the main user's photo slot
                val mainUserPhoto =
                    newestMemory.photos.firstOrNull { it.userId == "user_main" } ?: run {
                        Log.w("PhotoDetailViewModel", "Main user photo not found")
                        return@launch
                    }

                // Update that slot with the current photo
                updateMemoryPhotoUseCase(
                    communityId = communityId,
                    memoryId = newestMemory.id,
                    photoId = mainUserPhoto.id,
                    newPhotoUrl = photoUri
                )

                _uiState.update {
                    it.copy(
                        isLoading = false, navigationEvent = NavigationEvent.NavigateBack
                    )
                }
            } catch (e: Exception) {
                handleError(e, "Error creating new event")
                _uiState.update { it.copy(isLoading = false, showCommunitySelection = false) }
            }
        }
    }

    private fun updateAllMissingPhotos(currentPhotoUri: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                // Get list of missing photos
                val missingPhotos = uiState.value.missingPhotoInfo

                // Process each missing photo
                missingPhotos.forEach { missingPhotoInfo ->
                    val photoId = missingPhotoInfo.photo?.id ?: return@forEach

                    // Update each memory photo with current photo URI
                    updateMemoryPhotoUseCase(
                        communityId = missingPhotoInfo.communityId,
                        memoryId = missingPhotoInfo.memoryId,
                        photoId = photoId,
                        newPhotoUrl = currentPhotoUri
                    )
                }

                // Refresh the missing photo info list
                val updatedMissingInfo = getMissingPhotoInfoUseCase()

                _uiState.update {
                    it.copy(
                        missingPhotoInfo = updatedMissingInfo,
                        isLoading = false,
                        navigationEvent = NavigationEvent.NavigateBack
                    )
                }
            } catch (e: Exception) {
                handleError(e, "Error updating all missing photos")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun showCommunitySelector() {
        _uiState.update { it.copy(showCommunitySelection = true) }
    }

    private fun dismissCommunitySelector() {
        _uiState.update { it.copy(showCommunitySelection = false) }
    }

    private fun clearNavigationEvent() {
        _uiState.update { it.copy(navigationEvent = null) }
    }

    private fun handleError(exception: Exception, message: String) {
        Log.e("PhotoDetailViewModel", message, exception)
        // You could also dispatch errors to a channel for showing in the UI
    }
}

data class PhotoDetailUiState(
    val photo: Photo? = null,
    val isLoading: Boolean = false,
    val missingPhotoInfo: List<MissingPhotoInfo> = emptyList(),
    val userCommunities: List<Community> = emptyList(),
    val showCommunitySelection: Boolean = false,
    val navigationEvent: NavigationEvent? = null
)

sealed class NavigationEvent {
    object NavigateBack : NavigationEvent()
    object PhotoDeleted : NavigationEvent()
}

sealed class PhotoDetailEvent {
    data class LoadPhoto(val photoId: Int) : PhotoDetailEvent()
    data class DeletePhoto(val photoId: Int) : PhotoDetailEvent()
    data class UpdateMissingPhoto(
        val missingPhotoInfo: MissingPhotoInfo, val currentPhotoUri: String
    ) : PhotoDetailEvent()

    data class UpdateAllMissingPhotos(val currentPhotoUri: String) : PhotoDetailEvent()
    data class CreateEvent(val communityId: String, val photoUri: String) : PhotoDetailEvent()
    object ShowCommunitySelector : PhotoDetailEvent()
    object DismissCommunitySelector : PhotoDetailEvent()
    object NavigationHandled : PhotoDetailEvent()
}