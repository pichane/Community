package com.example.myapp.domain.model

data class User(
    val id: String,
    val userName: String,
    val profilePictureUrl: String,
    val isFriend: Boolean = false,
)
