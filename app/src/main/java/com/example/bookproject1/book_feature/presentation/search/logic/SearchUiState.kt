package com.example.bookproject1.book_feature.presentation.search.logic

import com.example.bookproject1.book_feature.domain.model.BookParagraph

data class SearchUiState(
    val query: String = "",
    val selectedPartFilter: Int? = null,
    val results: List<BookParagraph> = emptyList(),
    val filteredResults: List<BookParagraph> = emptyList(),
    val isLoading: Boolean = false,
    val isSearched: Boolean = false,
    val errorMessage: String? = null
)