package com.example.bookproject1.book_feature.presentation.search.logic

sealed interface SearchAction {
    data class OnQueryChange(val newQuery: String) : SearchAction
    data class OnFilterPartSelect(val partNumber: Int?) : SearchAction
    data object OnClearQuery : SearchAction
    data object OnSearchSubmit : SearchAction
}