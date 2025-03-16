package com.example.myapp.domain.model

import android.net.Uri

data class Photo(
    val id: Int = 0,
    val uri: Uri,
    val timestamp: Long,
    val title: String
)