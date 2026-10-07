package com.example.bookproject1.book_feature.domain.model

data class BookParagraph(
    val id: Int,
    val partNumber: Int,
    val orderIndex: Int,
    val mainSectionName: String,
    val mainSectionTitle: String?,
    val faslName: String?,
    val faslTitle: String?,
    val mabhathName: String?,
    val mabhathTitle: String?,
    val subheading: String?,
    val contentText: String
)