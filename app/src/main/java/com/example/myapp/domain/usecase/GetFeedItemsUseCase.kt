package com.example.myapp.domain.usecase

import com.example.myapp.domain.model.FeedItem
import com.example.myapp.domain.repository.FeedRepository

class GetFeedItemsUseCase(private val feedRepository: FeedRepository) {
    suspend operator fun invoke(): List<FeedItem> {
        return feedRepository.getFeedItems()
    }
}