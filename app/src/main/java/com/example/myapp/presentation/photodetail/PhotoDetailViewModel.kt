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
import com.example.myapp.domain.usecase.GetSelectedMissingPhotoUseCase
import com.example.myapp.domain.usecase.GetUserCommunitiesUseCase
import com.example.myapp.domain.usecase.SelectMissingPhotoUseCase
import com.example.myapp.domain.usecase.UpdateMemoryPhotoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PhotoDetailViewModel(
    private val getPhotoByIdUseCase: GetPhotoByIdUseCase,
    private val deletePhotoUseCase: DeletePhotoUseCase,
    private val getMissingPhotoInfoUseCase: GetMissingPhotoInfoUseCase,
    private val updateMemoryPhotoUseCase: UpdateMemoryPhotoUseCase,
    private val selectMissingPhotoUseCase: SelectMissingPhotoUseCase,
    private val getSelectedMissingPhotoUseCase: GetSelectedMissingPhotoUseCase,
    private val getUserCommunitiesUseCase: GetUserCommunitiesUseCase,
    private val addMemoryToCommunityUseCase: AddMemoryToCommunityUseCase
) : ViewModel() {

    private val _photo = MutableStateFlow<Photo?>(null)
    val photo: StateFlow<Photo?> = _photo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _missingPhotoInfo = MutableStateFlow<List<MissingPhotoInfo>>(emptyList())
    val missingPhotoInfo: StateFlow<List<MissingPhotoInfo>> = _missingPhotoInfo.asStateFlow()

    // Add user communities state
    val userCommunities: StateFlow<List<Community>> = getUserCommunitiesUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        loadMissingPhotoInfo()
    }

    fun loadPhoto(photoId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _photo.value = getPhotoByIdUseCase(photoId)
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadMissingPhotoInfo() {
        viewModelScope.launch {
            try {
                _missingPhotoInfo.value = getMissingPhotoInfoUseCase()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun deletePhoto(photoId: Int) {
        viewModelScope.launch {
            try {
                deletePhotoUseCase(photoId)
                // Photo deleted successfully
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun selectMissingPhotoForCapture(missingPhotoInfo: MissingPhotoInfo) {
        viewModelScope.launch {
            selectMissingPhotoUseCase(missingPhotoInfo)
        }
    }

    fun updateMissingPhoto(photoUri: String) {
        viewModelScope.launch {
            try {
                // Get the previously selected missing photo
                val missingPhotoInfo = getSelectedMissingPhotoUseCase() ?: return@launch

                // Only proceed if we have a valid photo reference
                val photoId = missingPhotoInfo.photo?.id ?: return@launch

                // Update the memory photo
                updateMemoryPhotoUseCase(
                    communityId = missingPhotoInfo.communityId,
                    memoryId = missingPhotoInfo.memoryId,
                    photoId = photoId,
                    newPhotoUrl = photoUri
                )

                // Refresh the missing photo info list
                _missingPhotoInfo.value = getMissingPhotoInfoUseCase()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateMissingPhotoWithCurrentPhoto(missingPhotoInfo: MissingPhotoInfo, currentPhotoUri: String) {
        viewModelScope.launch {
            try {
                // Only proceed if we have a valid photo reference
                val photoId = missingPhotoInfo.photo?.id ?: return@launch

                // Update the memory photo with current photo URI
                updateMemoryPhotoUseCase(
                    communityId = missingPhotoInfo.communityId,
                    memoryId = missingPhotoInfo.memoryId,
                    photoId = photoId,
                    newPhotoUrl = currentPhotoUri
                )

                // Refresh the missing photo info list
                _missingPhotoInfo.value = getMissingPhotoInfoUseCase()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun createNewEventWithPhoto(communityId: String, photoUri: String) {
        viewModelScope.launch {
            try {
                // Create event title based on date
                val dateFormat = SimpleDateFormat("MMMM dd", Locale.getDefault())
                val title = "Event on ${dateFormat.format(Date())}"

                // Create a new memory in the community
                addMemoryToCommunityUseCase(
                    communityId = communityId,
                    title = title,
                    date = Date()
                )

                // Get the most recently added memory - should be the first one
                val community = userCommunities.value.find { it.id == communityId } ?: return@launch
                if (community.memories.isEmpty()) return@launch

                val newestMemory = community.memories.first()

                // Find the main user's photo slot to update with the current photo
                val mainUserPhoto = newestMemory.photos.firstOrNull { it.userId == "user_main" } ?: return@launch

                // Update that slot with the current photo
                updateMemoryPhotoUseCase(
                    communityId = communityId,
                    memoryId = newestMemory.id,
                    photoId = mainUserPhoto.id,
                    newPhotoUrl = photoUri
                )

            } catch (e: Exception) {
                // Handle error
                Log.e("PhotoDetailViewModel", "Error creating event", e)
            }
        }
    }

    fun updateAllMissingPhotos(currentPhotoUri: String) {
        viewModelScope.launch {
            try {
                // Get list of missing photos
                val missingPhotos = _missingPhotoInfo.value

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
                // After updating all photos, the list should be empty
                _missingPhotoInfo.value = getMissingPhotoInfoUseCase()

            } catch (e: Exception) {
                Log.e("PhotoDetailViewModel", "Error updating all photos", e)
                // Handle error - could show a toast or error message
            }
        }
    }
}