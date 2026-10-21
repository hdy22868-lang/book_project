package com.example.bookproject1.book_feature.presentation.booktoc.logic

import com.example.bookproject1.book_feature.domain.model.TocNode

data class TocRow(
    val node: TocNode,
    val depth: Int
)