package com.example.myapp.data.repository

import android.net.Uri
import com.example.myapp.data.local.dao.PhotoDao
import com.example.myapp.data.local.entity.PhotoEntity
import com.example.myapp.domain.model.Photo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class PhotoRepositoryImplTest {

    @Mock
    private lateinit var photoDao: PhotoDao

    private lateinit var photoRepository: PhotoRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        photoRepository = PhotoRepositoryImpl(photoDao)
    }

    @Test
    fun `savePhoto converts Photo to Entity and calls dao`() = runTest {
        // Arrange
        val photo = Photo(
            id = 0,
            uri = Uri.parse("content://test/photo1"),
            timestamp = 1000L,
            title = "Test Photo"
        )
        val expectedId = 1L
        `when`(photoDao.insertPhoto(any())).thenReturn(expectedId)

        // Act
        val resultId = photoRepository.savePhoto(photo)

        // Assert
        assertEquals(expectedId, resultId)
        verify(photoDao).insertPhoto(
            match { entity ->
                entity.uri == photo.uri.toString() &&
                entity.timestamp == photo.timestamp &&
                entity.title == photo.title
            }
        )
    }

    @Test
    fun `getAllPhotos maps entities to domain models`() = runTest {
        // Arrange
        val photoEntities = listOf(
            PhotoEntity(id = 1, uri = "content://test/photo1", timestamp = 1000L, title = "Photo 1"),
            PhotoEntity(id = 2, uri = "content://test/photo2", timestamp = 2000L, title = "Photo 2")
        )
        `when`(photoDao.getAllPhotos()).thenReturn(flowOf(photoEntities))

        // Act
        val photos = photoRepository.getAllPhotos().first()

        // Assert
        assertEquals(2, photos.size)
        assertEquals(1, photos[0].id)
        assertEquals(Uri.parse("content://test/photo1"), photos[0].uri)
        assertEquals(1000L, photos[0].timestamp)
        assertEquals("Photo 1", photos[0].title)

        assertEquals(2, photos[1].id)
        assertEquals(Uri.parse("content://test/photo2"), photos[1].uri)
        assertEquals(2000L, photos[1].timestamp)
        assertEquals("Photo 2", photos[1].title)
    }

    @Test
    fun `getPhotoById returns correct photo`() = runTest {
        // Arrange
        val photoId = 1
        val photoEntity = PhotoEntity(
            id = photoId,
            uri = "content://test/photo1",
            timestamp = 1000L,
            title = "Photo 1"
        )
        `when`(photoDao.getPhotoById(photoId)).thenReturn(photoEntity)

        // Act
        val photo = photoRepository.getPhotoById(photoId)

        // Assert
        assertEquals(photoId, photo?.id)
        assertEquals(Uri.parse("content://test/photo1"), photo?.uri)
        assertEquals(1000L, photo?.timestamp)
        assertEquals("Photo 1", photo?.title)
    }

    @Test
    fun `getPhotoById returns null for non-existent photo`() = runTest {
        // Arrange
        val photoId = 999
        `when`(photoDao.getPhotoById(photoId)).thenReturn(null)

        // Act
        val photo = photoRepository.getPhotoById(photoId)

        // Assert
        assertNull(photo)
    }

    // Helper methods for Mockito argument matching
    private fun <T> any(): T {
        org.mockito.Mockito.any<T>()
        return null as T
    }

    private fun <T> match(matcher: (T) -> Boolean): T {
        org.mockito.ArgumentMatchers.argThat<T> { matcher(it) }
        return null as T
    }
}