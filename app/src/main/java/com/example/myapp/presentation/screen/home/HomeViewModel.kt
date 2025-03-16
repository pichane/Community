package com.example.myapp.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.domain.model.Community
import com.example.myapp.domain.model.Photo
import com.example.myapp.domain.usecase.GetAllPhotosUseCase
import com.example.myapp.domain.usecase.GetUserCommunitiesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getAllPhotosUseCase: GetAllPhotosUseCase,
    private val getUserCommunitiesUseCase: GetUserCommunitiesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.RefreshData -> loadData()
            HomeEvent.ErrorShown -> clearError()
        }
    }

    private fun loadData() {
        _uiState.update { it.copy(isLoading = true) }
        loadPhotos()
        loadCommunities()
    }

    private fun loadPhotos() {
        viewModelScope.launch {
            getAllPhotosUseCase()
                .catch { e ->
                    handleError(e)
                }
                .collect { photoList ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            photos = photoList,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun loadCommunities() {
        viewModelScope.launch {
            getUserCommunitiesUseCase()
                .catch { e ->
                    handleError(e)
                }
                .collect { communities ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            communities = communities
                        )
                    }
                }
        }
    }

    private fun handleError(throwable: Throwable) {
        _uiState.update {
            it.copy(
                error = throwable.message ?: "An unknown error occurred",
                isLoading = false
            )
        }
    }

    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class HomeUiState(
    val photos: List<Photo> = emptyList(),
    val communities: List<Community> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class HomeEvent {
    object RefreshData : HomeEvent()
    object ErrorShown : HomeEvent()
}
