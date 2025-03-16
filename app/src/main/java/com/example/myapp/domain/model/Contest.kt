package com.example.myapp.domain.model

import java.util.Date

data class Contest(
    val id: String,
    val title: String,
    val date: Date,
    val winners: List<ContestWinner>
)

data class ContestWinner(
    val id: String,
    val photoUrl: String,
    val emojiType: EmojiType,
    val userId: String
)

enum class EmojiType {
    CAMERA, // 📸 Real
    STAR,   // ⭐ Beautiful
    JOY     // 😂 Funny
}