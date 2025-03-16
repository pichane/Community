package com.example.myapp.presentation.social

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.domain.model.Community
import com.example.myapp.domain.model.User
import com.example.myapp.domain.usecase.AddFriendUseCase
import com.example.myapp.domain.usecase.GetDiscoveryCommunitiesUseCase
import com.example.myapp.domain.usecase.GetDiscoveryUsersUseCase
import com.example.myapp.domain.usecase.GetFriendsUseCase
import com.example.myapp.domain.usecase.GetUserCommunitiesUseCase
import com.example.myapp.domain.usecase.JoinCommunityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SocialViewModel(
    private val getDiscoveryUsersUseCase: GetDiscoveryUsersUseCase,
    private val getUserCommunitiesUseCase: GetUserCommunitiesUseCase,
    private val getDiscoveryCommunitiesUseCase: GetDiscoveryCommunitiesUseCase,
    private val getFriendsUseCase: GetFriendsUseCase,
    private val addFriendUseCase: AddFriendUseCase,
    private val joinCommunityUseCase: JoinCommunityUseCase
) : ViewModel() {

    // Single source of truth for UI state
    private val _uiState = MutableStateFlow(SocialUiState())
    val uiState: StateFlow<SocialUiState> = _uiState.asStateFlow()

    init {
        // Load initial data
        loadAllData()
    }

    fun onEvent(event: SocialEvent) {
        when (event) {
            is SocialEvent.AddFriend -> addFriend(event.userId)
            is SocialEvent.JoinCommunity -> joinCommunity(event.communityId)
            is SocialEvent.RefreshData -> loadAllData()
            SocialEvent.ErrorShown -> clearError()
        }
    }

    private fun loadAllData() {
        _uiState.update { it.copy(isLoading = true) }
        loadDiscoveryUsers()
        loadUserCommunities()
        loadDiscoveryCommunities()
        loadFriends()
    }

    private fun loadDiscoveryUsers() {
        viewModelScope.launch {
            try {
                getDiscoveryUsersUseCase()
                    .catch { exception ->
                        handleError(exception as Exception, "Failed to load discovery users")
                    }
                    .collect { users ->
                        _uiState.update { it.copy(discoveryUsers = users, isLoading = false) }
                    }
            } catch (e: Exception) {
                handleError(e, "Error loading discovery users")
            }
        }
    }

    private fun loadUserCommunities() {
        viewModelScope.launch {
            try {
                getUserCommunitiesUseCase()
                    .catch { exception ->
                        handleError(exception as Exception, "Failed to load user communities")
                    }
                    .collect { communities ->
                        _uiState.update { it.copy(userCommunities = communities, isLoading = false) }
                    }
            } catch (e: Exception) {
                handleError(e, "Error loading user communities")
            }
        }
    }

    private fun loadDiscoveryCommunities() {
        viewModelScope.launch {
            try {
                getDiscoveryCommunitiesUseCase()
                    .catch { exception ->
                        handleError(exception as Exception, "Failed to load discovery communities")
                    }
                    .collect { communities ->
                        _uiState.update { it.copy(discoveryCommunities = communities, isLoading = false) }
                    }
            } catch (e: Exception) {
                handleError(e, "Error loading discovery communities")
            }
        }
    }

    private fun loadFriends() {
        viewModelScope.launch {
            try {
                getFriendsUseCase()
                    .catch { exception ->
                        handleError(exception as Exception, "Failed to load friends")
                    }
                    .collect { friends ->
                        _uiState.update { it.copy(friends = friends, isLoading = false) }
                    }
            } catch (e: Exception) {
                handleError(e, "Error loading friends")
            }
        }
    }

    private fun addFriend(userId: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                addFriendUseCase(userId)

                // Refresh relevant data after adding friend
                loadDiscoveryUsers()
                loadFriends()

                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                handleError(e, "Failed to add friend")
            }
        }
    }

    private fun joinCommunity(communityId: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                joinCommunityUseCase(communityId)

                // Refresh relevant data after joining community
                loadUserCommunities()
                loadDiscoveryCommunities()

                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                handleError(e, "Failed to join community")
            }
        }
    }

    private fun handleError(exception: Exception, message: String) {
        Log.e("SocialViewModel", message, exception)
        _uiState.update {
            it.copy(
                error = exception.message ?: message,
                isLoading = false
            )
        }
    }

    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class SocialUiState(
    val discoveryUsers: List<User> = emptyList(),
    val userCommunities: List<Community> = emptyList(),
    val discoveryCommunities: List<Community> = emptyList(),
    val friends: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class SocialEvent {
    data class AddFriend(val userId: String) : SocialEvent()
    data class JoinCommunity(val communityId: String) : SocialEvent()
    object RefreshData : SocialEvent()
    object ErrorShown : SocialEvent()
}
