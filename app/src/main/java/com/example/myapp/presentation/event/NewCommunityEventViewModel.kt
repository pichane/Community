package com.example.myapp.presentation.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.domain.model.Community
import com.example.myapp.domain.usecase.AddMemoryToCommunityUseCase
import com.example.myapp.domain.usecase.GetUserCommunitiesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

class NewCommunityEventViewModel(
    private val getUserCommunitiesUseCase: GetUserCommunitiesUseCase,
    private val addMemoryToCommunityUseCase: AddMemoryToCommunityUseCase
) : ViewModel() {

    // UI state
    private val _uiState = MutableStateFlow(NewEventUiState())
    val uiState: StateFlow<NewEventUiState> = _uiState.asStateFlow()

    // List of user communities
    val userCommunities: StateFlow<List<Community>> = getUserCommunitiesUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /**
     * Updates the event title field
     */
    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(
            title = title,
            isTitleError = title.isBlank()
        )
    }

    /**
     * Creates a new memory for a community
     */
    fun createNewMemory(communityId: String) {
        val currentState = _uiState.value

        // Validate title
        if (currentState.title.isBlank()) {
            _uiState.value = currentState.copy(isTitleError = true)
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true)

            try {
                addMemoryToCommunityUseCase(
                    communityId = communityId,
                    title = currentState.title,
                    date = Date()
                )
                _uiState.value = currentState.copy(
                    isLoading = false,
                    isSuccessful = true
                )
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error creating event"
                )
            }
        }
    }
}

/**
 * Data class representing the UI state for the New Event screen
 */
data class NewEventUiState(
    val title: String = "",
    val isTitleError: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccessful: Boolean = false,
    val errorMessage: String? = null
)
