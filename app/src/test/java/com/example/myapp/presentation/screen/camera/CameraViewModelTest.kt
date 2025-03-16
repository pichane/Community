package com.example.myapp.presentation.screen.camera

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.camera.core.ImageCapture
import com.example.myapp.domain.usecase.CapturePhotoUseCase
import com.example.myapp.domain.usecase.SavePhotoUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class CameraViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var savePhotoUseCase: SavePhotoUseCase

    @Mock
    private lateinit var capturePhotoUseCase: CapturePhotoUseCase

    @Mock
    private lateinit var imageCaptureWrapper: ImageCapture

    @Mock
    private lateinit var mockUri: Uri

    private lateinit var cameraViewModel: CameraViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        cameraViewModel = CameraViewModel(savePhotoUseCase, capturePhotoUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() {
        // Assert
        val initialState = cameraViewModel.uiState.value
        assertFalse(initialState.isCameraReady)
        assertFalse(initialState.isCapturing)
        assertNull(initialState.error)
        assertNull(initialState.lastCapturedPhotoUri)
        assertEquals(0L, initialState.lastPhotoTimestamp)
    }

    @Test
    fun `setCameraReady updates state`() {
        // Act
        cameraViewModel.onEvent(CameraEvent.SetCameraReady(true))

        // Assert
        val state = cameraViewModel.uiState.value
        assertTrue(state.isCameraReady)
    }

    @Test
    fun `capturePhoto updates error state on failure`() = runTest {
        // Arrange
        val errorMessage = "Failed to capture photo"
        `when`(capturePhotoUseCase(imageCaptureWrapper)).thenThrow(RuntimeException(errorMessage))

        // Act
        cameraViewModel.capturePhoto(imageCaptureWrapper)

        // Assert
        val state = cameraViewModel.uiState.value
        assertFalse(state.isCapturing)
        assertEquals(errorMessage, state.error)
    }

}