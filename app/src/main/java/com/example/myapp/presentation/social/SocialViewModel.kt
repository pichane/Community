package com.example.myapp.presentation.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.domain.model.Community
import com.example.myapp.domain.model.User
import com.example.myapp.domain.repository.SocialRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SocialViewModel(
    private val socialRepository: SocialRepository
) : ViewModel() {

    val discoveryUsers: StateFlow<List<User>> = socialRepository.getDiscoveryUsers()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val userCommunities: StateFlow<List<Community>> = socialRepository.getUserCommunities()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val discoveryCommunities: StateFlow<List<Community>> = socialRepository.getDiscoveryCommunities()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val friends: StateFlow<List<User>> = socialRepository.getFriends()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addFriend(userId: String) {
        viewModelScope.launch {
            socialRepository.addFriend(userId)
        }
    }

    fun removeFriend(userId: String) {
        viewModelScope.launch {
            socialRepository.removeFriend(userId)
        }
    }

    fun joinCommunity(communityId: String) {
        viewModelScope.launch {
            socialRepository.joinCommunity(communityId)
        }
    }

    fun leaveCommunity(communityId: String) {
        viewModelScope.launch {
            socialRepository.leaveCommunity(communityId)
        }
    }
}