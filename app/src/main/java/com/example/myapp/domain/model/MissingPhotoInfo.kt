package com.example.myapp.domain.model

data class MissingPhotoInfo(
    val communityId: String,
    val communityName: String,
    val memoryId: String,
    val memoryTitle: String,
    val photo: FromUser? = null
)