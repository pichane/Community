package com.example.myapp.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.domain.model.Community
import com.example.myapp.domain.model.Photo
import com.example.myapp.domain.usecase.GetAllPhotosUseCase
import com.example.myapp.domain.usecase.GetUserCommunitiesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getAllPhotosUseCase: GetAllPhotosUseCase,
    private val getUserCommunitiesUseCase: GetUserCommunitiesUseCase,
) : ViewModel() {

    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val userCommunities: StateFlow<List<Community>> = getUserCommunitiesUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        loadPhotos()
    }

    fun loadPhotos() {
        viewModelScope.launch {
            getAllPhotosUseCase().collect { photoList ->
                _photos.value = photoList
            }
        }
    }
}