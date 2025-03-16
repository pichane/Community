package com.example.myapp.data.repository

import android.net.Uri
import com.example.myapp.data.local.dao.PhotoDao
import com.example.myapp.data.local.entity.PhotoEntity
import com.example.myapp.domain.model.Contest
import com.example.myapp.domain.model.ContestWinner
import com.example.myapp.domain.model.EmojiType
import com.example.myapp.domain.model.Photo
import com.example.myapp.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.Date

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
    override suspend fun getContests(): List<Contest> {
        val currentDate = Date()
        val calendar = Calendar.getInstance()

        // Create dates for multiple contests
        calendar.add(Calendar.DAY_OF_MONTH, -3)
        val threeDaysAgo = calendar.time

        calendar.add(Calendar.DAY_OF_MONTH, -4)
        val weekAgo = calendar.time

        return listOf(
            Contest(
                id = "contest1",
                title = "Nature Photography",
                date = currentDate,
                winners = listOf(
                    ContestWinner(
                        id = "winner1",
                        photoUrl = "https://picsum.photos/seed/contest1/300/400",
                        emojiType = EmojiType.STAR,
                        userId = "user1"
                    ),
                    ContestWinner(
                        id = "winner2",
                        photoUrl = "https://picsum.photos/seed/contest2/300/400",
                        emojiType = EmojiType.JOY,
                        userId = "user2"
                    ),
                    ContestWinner(
                        id = "winner3",
                        photoUrl = "https://picsum.photos/seed/contest3/300/400",
                        emojiType = EmojiType.CAMERA,
                        userId = "user3"
                    )
                )
            ),
            Contest(
                id = "contest2",
                title = "Urban Landscapes",
                date = threeDaysAgo,
                winners = listOf(
                    ContestWinner(
                        id = "winner4",
                        photoUrl = "https://picsum.photos/seed/contest4/300/400",
                        emojiType = EmojiType.STAR,
                        userId = "user4"
                    ),
                    ContestWinner(
                        id = "winner5",
                        photoUrl = "https://picsum.photos/seed/contest5/300/400",
                        emojiType = EmojiType.JOY,
                        userId = "user5"
                    ),
                    ContestWinner(
                        id = "winner6",
                        photoUrl = "https://picsum.photos/seed/contest6/300/400",
                        emojiType = EmojiType.CAMERA,
                        userId = "user6"
                    )
                )
            ),
            Contest(
                id = "contest3",
                title = "Pet Photos",
                date = weekAgo,
                winners = listOf(
                    ContestWinner(
                        id = "winner7",
                        photoUrl = "https://picsum.photos/seed/contest7/300/400",
                        emojiType = EmojiType.STAR,
                        userId = "user7"
                    ),
                    ContestWinner(
                        id = "winner8",
                        photoUrl = "https://picsum.photos/seed/contest8/300/400",
                        emojiType = EmojiType.JOY,
                        userId = "user8"
                    ),
                    ContestWinner(
                        id = "winner9",
                        photoUrl = "https://picsum.photos/seed/contest9/300/400",
                        emojiType = EmojiType.CAMERA,
                        userId = "user9"
                    )
                )
            )
        )
    }
}