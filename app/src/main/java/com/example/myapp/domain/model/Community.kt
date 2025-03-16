package com.example.myapp.domain.model

data class Community(
    val id: String,
    val userName: String,
    val profilePictureUrl: String,
    val isFriend: Boolean = false,
    val isCommunityMember: Boolean = false,
    val memories: List<Memory>
)
