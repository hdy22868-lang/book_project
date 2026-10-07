package com.example.bookproject1.book_feature.domain.model

data class TocItem(
    val mainSectionName: String,
    val mainSectionTitle: String?,
    val faslName: String?,
    val faslTitle: String?,
    val mabhathName: String?,
    val mabhathTitle: String?,
    val startIndex: Int,
    val endIndex: Int
)