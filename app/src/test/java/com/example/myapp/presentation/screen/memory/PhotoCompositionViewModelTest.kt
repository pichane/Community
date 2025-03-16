package com.example.myapp.presentation.screen.memory

import com.example.myapp.domain.model.Memory
import com.example.myapp.domain.usecase.MemoryResult
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertFalse
import org.junit.Test
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.`when`
import org.mockito.kotlin.doReturn

@org.junit.runner.RunWith(org.robolectric.RobolectricTestRunner::class)
@kotlinx.coroutines.ExperimentalCoroutinesApi
class PhotoCompositionViewModelTest {

    @get:org.junit.Rule
    val testInstantTaskExecutorRule: org.junit.rules.TestRule =
        androidx.arch.core.executor.testing.InstantTaskExecutorRule()

    private val testDispatcher: kotlinx.coroutines.test.TestDispatcher =
        kotlinx.coroutines.test.UnconfinedTestDispatcher()

    @org.mockito.Mock
    private lateinit var getMemoryUseCase: com.example.myapp.domain.usecase.GetMemoryUseCase

    private lateinit var viewModel: PhotoCompositionViewModel

    @org.junit.Before
    fun setup() {
        org.mockito.MockitoAnnotations.openMocks(this)
        kotlinx.coroutines.Dispatchers.setMain(testDispatcher)
    }

    @org.junit.After
    fun tearDown() {
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    @org.junit.Test
    fun `initial state is correct`() = kotlinx.coroutines.test.runTest {
        // Act
        viewModel = PhotoCompositionViewModel(getMemoryUseCase)

        // Assert
        val initialState = viewModel.uiState.value
        org.junit.Assert.assertNull(initialState.memory)
        org.junit.Assert.assertNull(initialState.error)
        org.junit.Assert.assertEquals("", initialState.communityName)
    }

    @org.junit.Test
    fun `loadMemory success updates state correctly`() = kotlinx.coroutines.test.runTest {
        // Arrange
        val communityId = "community1"
        val memoryId = "memory1"
        val memory = Memory(
            id = memoryId,
            communityId = communityId,
            title = "Test Memory",
            isHorizontal = true,
            photos = listOf(
                com.example.myapp.domain.model.FromUser("user1", "photo1.jpg", "1000L")
            )
        )
        val communityName = "Test Community"

        `when`(getMemoryUseCase(communityId, memoryId)).thenReturn(
            MemoryResult(memory, communityName)
        )

        viewModel = PhotoCompositionViewModel(getMemoryUseCase)

        // Act
        viewModel.loadMemory(communityId, memoryId)

        // Assert
        val state = viewModel.uiState.value
        org.junit.Assert.assertEquals(memory, state.memory)
        org.junit.Assert.assertEquals(communityName, state.communityName)
        org.junit.Assert.assertFalse(state.isLoading)
        org.junit.Assert.assertNull(state.error)
    }

    @org.junit.Test
    fun `loadMemory with null memory updates error state`() = kotlinx.coroutines.test.runTest {
        // Arrange
        val communityId = "community1"
        val memoryId = "memory1"

        `when`(getMemoryUseCase(communityId, memoryId)).thenReturn(
            MemoryResult(null, null)
        )

        viewModel = PhotoCompositionViewModel(getMemoryUseCase)

        // Act
        viewModel.loadMemory(communityId, memoryId)

        // Assert
        val state = viewModel.uiState.value
        org.junit.Assert.assertEquals("Memory not found", state.error)
        org.junit.Assert.assertFalse(state.isLoading)
    }

    @Test
    fun `loadMemory exception updates error state`() = kotlinx.coroutines.test.runTest {
        // Arrange
        val communityId = "community1"
        val memoryId = "memory1"
        val errorMessage = "Failed to load memory"

        `when`(getMemoryUseCase(communityId, memoryId)).thenThrow(
            RuntimeException(errorMessage)
        )

        viewModel = PhotoCompositionViewModel(getMemoryUseCase)

        // Act
        viewModel.loadMemory(communityId, memoryId)

        // Assert
        val state = viewModel.uiState.value
        org.junit.Assert.assertEquals(errorMessage, state.error)
        org.junit.Assert.assertFalse(state.isLoading)
    }


    @Test
    fun `retry load calls loadMemory with last parameters`() = runTest {
        // Arrange
        val communityId = "community1"
        val memoryId = "memory1"

        // First attempt fails
        doAnswer {
            throw RuntimeException("Failed")
        }.`when`(getMemoryUseCase).invoke(communityId, memoryId)

        viewModel = PhotoCompositionViewModel(getMemoryUseCase)
        viewModel.loadMemory(communityId, memoryId)

        // Second attempt succeeds
        val memory = Memory(
            id = memoryId,
            communityId = communityId,
            title = "Test Memory",
            isHorizontal = true,
            photos = listOf()
        )

        doReturn(
            MemoryResult(memory, "Community")
        ).`when`(getMemoryUseCase).invoke(communityId, memoryId)

        // Act
        viewModel.retryLoad()

        // Assert
        val state = viewModel.uiState.value
        assertEquals(memory, state.memory)
        assertFalse(state.isLoading)
    }
}
