package com.example.myapp.domain.repository

import com.example.myapp.domain.model.FeedItem

interface FeedRepository {
    suspend fun getFeedItems(): List<FeedItem>
}