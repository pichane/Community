package com.example.myapp.domain.repository

import com.example.myapp.domain.model.Contest
import com.example.myapp.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    suspend fun savePhoto(photo: Photo): Long
    fun getAllPhotos(): Flow<List<Photo>>
    suspend fun getPhotoById(id: Int): Photo?
    suspend fun deletePhoto(id: Int)
    suspend fun getContests(): List<Contest>
}
