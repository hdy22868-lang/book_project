package com.example.bookproject1.book_feature.presentation.booktoc.logic

import com.example.bookproject1.book_feature.domain.model.TocNode

data class BookTocUiState(
    val partNumber: Int = 0,
    val nodes: List<TocNode> = emptyList(),
    val expandedIds: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    val isError: Boolean = false
)