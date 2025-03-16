package com.example.myapp.domain.usecase

import android.net.Uri
import com.example.myapp.domain.model.Photo
import com.example.myapp.domain.repository.PhotoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class GetPhotoByIdUseCaseTest {

    @Mock
    private lateinit var photoRepository: PhotoRepository

    private lateinit var getPhotoByIdUseCase: GetPhotoByIdUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getPhotoByIdUseCase = GetPhotoByIdUseCase(photoRepository)
    }

    @Test
    fun `invoke returns photo when found`() = runTest {
        // Arrange
        val photoId = 42
        val expectedPhoto = Photo(
            id = photoId,
            uri = Uri.parse("content://test/photo"),
            timestamp = 1000L,
            title = "Test Photo"
        )

        `when`(photoRepository.getPhotoById(photoId)).thenReturn(expectedPhoto)

        // Act
        val result = getPhotoByIdUseCase(photoId)

        // Assert
        assertEquals(expectedPhoto, result)
    }

    @Test
    fun `invoke returns null when photo not found`() = runTest {
        // Arrange
        val photoId = 404

        `when`(photoRepository.getPhotoById(photoId)).thenReturn(null)

        // Act
        val result = getPhotoByIdUseCase(photoId)

        // Assert
        assertNull(result)
    }
}