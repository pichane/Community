package com.example.myapp.presentation.screen.memory

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.domain.model.Memory
import com.example.myapp.domain.usecase.GetMemoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PhotoCompositionViewModel(
    private val getMemoryUseCase: GetMemoryUseCase
) : ViewModel() {

    // UI state
    private val _uiState = MutableStateFlow(PhotoCompositionUiState())
    val uiState: StateFlow<PhotoCompositionUiState> = _uiState.asStateFlow()

    // Direct method calls instead of event handlers
    fun loadMemory(communityId: String, memoryId: String) {
        _uiState.update {
            it.copy(
                isLoading = true,
                error = null,
                lastLoadParams = LoadParams(communityId, memoryId)
            )
        }

        viewModelScope.launch {
            try {
                val result = getMemoryUseCase(communityId, memoryId)

                if (result.memory != null) {
                    _uiState.update {
                        it.copy(
                            memory = result.memory,
                            communityName = result.communityName ?: "",
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            error = "Memory not found",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    fun retryLoad() {
        val lastLoadParams = _uiState.value.lastLoadParams
        if (lastLoadParams != null) {
            loadMemory(lastLoadParams.communityId, lastLoadParams.memoryId)
        }
    }

    fun errorShown() {
        _uiState.update { it.copy(error = null) }
    }

    private fun handleError(exception: Exception) {
        Log.e(TAG, "Error loading memory", exception)
        _uiState.update {
            it.copy(
                error = exception.message ?: "Unknown error occurred",
                isLoading = false
            )
        }
    }

    companion object {
        private const val TAG = "PhotoCompViewModel"
    }
}

// Single state object to represent the entire UI state
data class PhotoCompositionUiState(
    val memory: Memory? = null,
    val communityName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastLoadParams: LoadParams? = null
)

// Parameter object for keeping track of last loaded parameters for retry functionality
data class LoadParams(
    val communityId: String,
    val memoryId: String
)