package com.example.myapp.domain.usecase

import com.example.myapp.domain.repository.SocialRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class UpdateMemoryPhotoUseCaseTest {

    @Mock
    private lateinit var socialRepository: SocialRepository

    private lateinit var updateMemoryPhotoUseCase: UpdateMemoryPhotoUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        updateMemoryPhotoUseCase = UpdateMemoryPhotoUseCase(socialRepository)
    }

    @Test
    fun `invoke calls repository updateMemoryPhoto with correct parameters`() = runTest {
        // Arrange
        val communityId = "community1"
        val memoryId = "memory1"
        val photoId = "photo1"
        val photoUrl = "https://example.com/updated-photo.jpg"

        // Act
        updateMemoryPhotoUseCase(communityId, memoryId, photoId, photoUrl)

        // Assert
        verify(socialRepository).updateMemoryPhoto(communityId, memoryId, photoId, photoUrl)
    }
}