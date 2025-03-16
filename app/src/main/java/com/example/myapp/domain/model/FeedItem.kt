package com.example.myapp.domain.model

data class FeedItem(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String? = null
)