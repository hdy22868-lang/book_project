package com.example.bookproject1.book_feature.presentation.home.logic

import com.example.bookproject1.book_feature.presentation.component.logic.BookPart

data class HomeUiState(
    val parts: List<BookPart> = BookPart.defaultParts,
    val isGridLayout: Boolean = true
)