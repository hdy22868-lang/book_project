package com.example.bookproject1.book_feature.presentation.favorites.logic

import com.example.bookproject1.book_feature.domain.model.BookHighlight

data class FavoritesUiState(
    val allHighlights: List<BookHighlight> = emptyList(),
    val selectedPartFilter: Int? = null
) {
    val filteredHighlights: List<BookHighlight>
        get() = if (selectedPartFilter == null) {
            allHighlights
        } else {
            allHighlights.filter { it.partNumber == selectedPartFilter }
        }
}