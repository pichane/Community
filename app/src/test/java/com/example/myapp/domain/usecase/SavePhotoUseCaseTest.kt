package com.example.myapp.domain.usecase

import android.net.Uri
import com.example.myapp.domain.model.Photo
import com.example.myapp.domain.repository.PhotoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class SavePhotoUseCaseTest {

    @Mock
    private lateinit var photoRepository: PhotoRepository

    private lateinit var savePhotoUseCase: SavePhotoUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        savePhotoUseCase = SavePhotoUseCase(photoRepository)
    }

    @Test
    fun `invoke saves photo and returns id`() = runTest {
        // Arrange
        val photo = Photo(
            id = 0,
            uri = Uri.parse("content://test/photo"),
            timestamp = 1000L,
            title = "Test Photo"
        )
        val expectedId = 42L

        `when`(photoRepository.savePhoto(photo)).thenReturn(expectedId)

        // Act
        val result = savePhotoUseCase(photo)

        // Assert
        assertEquals(expectedId, result)
    }
}