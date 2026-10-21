package com.example.bookproject1.book_feature.domain.model

data class BookHighlight(
    val id: Int = 0,
    val partNumber: Int,
    val paragraphId: Int,
    val sectionTitle: String,
    val selectedText: String,
    val colorHex: Long = 0xFFFFD54F,
    val createdAt: Long = System.currentTimeMillis()
)