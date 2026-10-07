package com.example.bookproject1.book_feature.domain.model

data class BookParagraph(
    val id: Int,
    val partNumber: Int,
    val sectionTitle: String,
    val subheading: String?,
    val contentText: String
)