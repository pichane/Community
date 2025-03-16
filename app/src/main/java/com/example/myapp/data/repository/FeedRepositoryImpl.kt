package com.example.myapp.data.repository

import com.example.myapp.domain.model.FeedItem
import com.example.myapp.domain.repository.FeedRepository

class FeedRepositoryImpl : FeedRepository {
    override suspend fun getFeedItems(): List<FeedItem> {
        // Hardcoded data for the feed
        return listOf(
            FeedItem(
                id = 1,
                title = "First Item",
                description = "This is the description for the first item in our feed."
            ),
            FeedItem(
                id = 2,
                title = "Second Item",
                description = "This is the description for the second item in our feed."
            ),
            FeedItem(
                id = 3,
                title = "Third Item",
                description = "This is the description for the third item in our feed."
            ),
            FeedItem(
                id = 4,
                title = "Fourth Item",
                description = "This is the description for the fourth item in our feed."
            ),
            FeedItem(
                id = 5,
                title = "Fifth Item",
                description = "This is the description for the fifth item in our feed."
            ),
            FeedItem(
                id = 1,
                title = "First Item",
                description = "This is the description for the first item in our feed."
            ),
            FeedItem(
                id = 2,
                title = "Second Item",
                description = "This is the description for the second item in our feed."
            ),
            FeedItem(
                id = 3,
                title = "Third Item",
                description = "This is the description for the third item in our feed."
            ),
            FeedItem(
                id = 4,
                title = "Fourth Item",
                description = "This is the description for the fourth item in our feed."
            ),
            FeedItem(
                id = 5,
                title = "Fifth Item",
                description = "This is the description for the fifth item in our feed."
            ),
            FeedItem(
                id = 1,
                title = "First Item",
                description = "This is the description for the first item in our feed."
            ),
            FeedItem(
                id = 2,
                title = "Second Item",
                description = "This is the description for the second item in our feed."
            ),
            FeedItem(
                id = 3,
                title = "Third Item",
                description = "This is the description for the third item in our feed."
            ),
            FeedItem(
                id = 4,
                title = "Fourth Item",
                description = "This is the description for the fourth item in our feed."
            ),
            FeedItem(
                id = 5,
                title = "Fifth Item",
                description = "This is the description for the fifth item in our feed."
            )
        )
    }
}