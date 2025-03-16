package com.example.myapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.domain.model.Community
import com.example.myapp.domain.model.Contest
import com.example.myapp.domain.model.FeedItem
import com.example.myapp.domain.model.Memory
import com.example.myapp.domain.model.Photo
import com.example.myapp.domain.model.User
import com.example.myapp.domain.repository.SocialRepository
import com.example.myapp.domain.usecase.GetAllPhotosUseCase
import com.example.myapp.domain.usecase.GetCommunityPhotosUseCase
import com.example.myapp.domain.usecase.GetContestsUseCase
import com.example.myapp.domain.usecase.GetFeedItemsUseCase
import com.example.myapp.domain.usecase.GetUserCommunitiesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getFeedItemsUseCase: GetFeedItemsUseCase,
    private val getAllPhotosUseCase: GetAllPhotosUseCase,
    private val getUserCommunitiesUseCase: GetUserCommunitiesUseCase,
    private val getCommunityPhotosUseCase: GetCommunityPhotosUseCase,
    private val getContestsUseCase: GetContestsUseCase
) : ViewModel() {

    private val _feedItems = MutableStateFlow<List<FeedItem>>(emptyList())
    val feedItems: StateFlow<List<FeedItem>> = _feedItems.asStateFlow()

    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val userCommunities: StateFlow<List<Community>> = getUserCommunitiesUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _contests = MutableStateFlow<List<Contest>>(emptyList())
    val contests: StateFlow<List<Contest>> = _contests.asStateFlow()

    init {
        loadFeedItems()
        loadPhotos()
        viewModelScope.launch {
            try {
                _contests.value = getContestsUseCase()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun loadFeedItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _feedItems.value = getFeedItemsUseCase()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadPhotos() {
        viewModelScope.launch {
            getAllPhotosUseCase().collect { photoList ->
                _photos.value = photoList
            }
        }
    }
}