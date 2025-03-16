package com.example.myapp.domain.usecase

import com.example.myapp.domain.repository.PhotoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class DeletePhotoUseCaseTest {

    @Mock
    private lateinit var photoRepository: PhotoRepository

    private lateinit var deletePhotoUseCase: DeletePhotoUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        deletePhotoUseCase = DeletePhotoUseCase(photoRepository)
    }

    @Test
    fun `invoke calls repository deletePhoto method with correct ID`() = runTest {
        // Arrange
        val photoId = 42

        // Act
        deletePhotoUseCase(photoId)

        // Assert
        verify(photoRepository).deletePhoto(photoId)
    }
}