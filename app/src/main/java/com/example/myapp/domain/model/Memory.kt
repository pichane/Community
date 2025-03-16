package com.example.myapp.domain.model

data class Memory(
    val id: String,
    val communityId: String,
    val title: String,
    val isHorizontal: Boolean,
    val photos: List<FromUser>
)

data class FromUser(
    val id: String,
    val userId: String,
    val url: String
)
