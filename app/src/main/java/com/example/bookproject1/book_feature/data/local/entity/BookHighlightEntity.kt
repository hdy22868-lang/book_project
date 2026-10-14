package com.example.bookproject1.book_feature.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_highlights")
data class BookHighlightEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val partNumber: Int,
    val paragraphId: Int,
    val sectionTitle: String,
    val selectedText: String,
    val colorHex: Long = 0xFFFFD54F,
    val createdAt: Long = System.currentTimeMillis()
)