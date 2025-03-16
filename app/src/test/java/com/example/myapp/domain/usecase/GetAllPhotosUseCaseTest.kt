package com.example.myapp.domain.usecase

import android.net.Uri
import com.example.myapp.domain.model.Photo
import com.example.myapp.domain.repository.PhotoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
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
class GetAllPhotosUseCaseTest {

    @Mock
    private lateinit var photoRepository: PhotoRepository

    private lateinit var getAllPhotosUseCase: GetAllPhotosUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getAllPhotosUseCase = GetAllPhotosUseCase(photoRepository)
    }

    @Test
    fun `invoke returns photos from repository`() = runTest {
        // Arrange
        val expectedPhotos = listOf(
            Photo(1, Uri.parse("content://test/photo1"), 1000L, "Photo 1"),
            Photo(2, Uri.parse("content://test/photo2"), 2000L, "Photo 2")
        )

        `when`(photoRepository.getAllPhotos()).thenReturn(flow { emit(expectedPhotos) })

        // Act
        val actualPhotos = getAllPhotosUseCase().first()

        // Assert
        assertEquals(expectedPhotos, actualPhotos)
        assertEquals(2, actualPhotos.size)
        assertEquals(1, actualPhotos[0].id)
        assertEquals("Photo 1", actualPhotos[0].title)
        assertEquals(2, actualPhotos[1].id)
        assertEquals("Photo 2", actualPhotos[1].title)
    }

    @Test
    fun `invoke returns empty list when repository returns empty`() = runTest {
        // Arrange
        val emptyList = emptyList<Photo>()
        `when`(photoRepository.getAllPhotos()).thenReturn(flow { emit(emptyList) })

        // Act
        val result = getAllPhotosUseCase().first()

        // Assert
        assertEquals(emptyList, result)
    }
}