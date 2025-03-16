package com.example.myapp.data.repository

import android.net.Uri
import com.example.myapp.data.local.dao.PhotoDao
import com.example.myapp.data.local.entity.PhotoEntity
import com.example.myapp.domain.model.Photo
import com.example.myapp.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PhotoRepositoryImpl(
    private val photoDao: PhotoDao
) : PhotoRepository {

    override suspend fun savePhoto(photo: Photo): Long {
        // Persist URI as string
        val entity = PhotoEntity(
            id = photo.id,
            uri = photo.uri.toString(),
            timestamp = photo.timestamp,
            title = photo.title
        )

        // Save to Room database
        return photoDao.insertPhoto(entity)
    }

    override fun getAllPhotos(): Flow<List<Photo>> {
        return photoDao.getAllPhotos().map { entities ->
            entities.map { entity ->
                Photo(
                    id = entity.id,
                    uri = Uri.parse(entity.uri),
                    timestamp = entity.timestamp,
                    title = entity.title
                )
            }
        }
    }

    override suspend fun getPhotoById(id: Int): Photo? {
        return photoDao.getPhotoById(id)?.let { entity ->
            Photo(
                id = entity.id,
                uri = Uri.parse(entity.uri),
                timestamp = entity.timestamp,
                title = entity.title
            )
        }
    }

    override suspend fun deletePhoto(id: Int) {
        photoDao.deletePhoto(id)
    }
}
