package com.example.bookproject1.book_feature.presentation.reading.logic

import com.example.bookproject1.book_feature.domain.model.BookParagraph

data class BookReadingUiState(
    val paragraphs: List<BookParagraph> = emptyList(),
    val title: String = "",
    val subtitle: String? = null,
    val highlightsMap: Map<Int, List<TextHighlight>> = emptyMap(),
    val targetParagraphId: Int? = null,
    val isLoading: Boolean = true,
    val isError: Boolean = false
)