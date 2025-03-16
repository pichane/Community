package com.example.myapp.presentation.memory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.domain.model.Memory
import com.example.myapp.domain.usecase.GetMemoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class PhotoCompositionViewModel(
    private val getMemoryUseCase: GetMemoryUseCase
) : ViewModel() {

    // UI state to represent the current state of the screen
    private val _uiState = MutableStateFlow<MemoryUiState>(MemoryUiState.Loading)
    val uiState: StateFlow<MemoryUiState> = _uiState.asStateFlow()

    // Community name for display in the header
    private val _communityName = MutableStateFlow("")
    val communityName: StateFlow<String> = _communityName.asStateFlow()

    fun loadMemory(communityId: String, memoryId: String) {
        _uiState.value = MemoryUiState.Loading

        viewModelScope.launch {
            try {
                val memoryResult = getMemoryUseCase(communityId, memoryId)

                if (memoryResult.memory != null) {
                    _uiState.value = MemoryUiState.Success(memoryResult.memory)
                    _communityName.value = memoryResult.communityName ?: ""
                } else {
                    _uiState.value = MemoryUiState.Error("Memory not found")
                }
            } catch (e: Exception) {
                _uiState.value = MemoryUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}

/**
 * Sealed class representing all possible UI states
 */
sealed class MemoryUiState {
    /**
     * State when data is being loaded
     */
    object Loading : MemoryUiState()

    /**
     * State when data is successfully loaded
     */
    data class Success(val memory: Memory) : MemoryUiState()

    /**
     * State when an error occurs while loading data
     */
    data class Error(val message: String) : MemoryUiState()
}