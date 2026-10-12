package com.example.bookproject1.book_feature.presentation.home.logic

import com.example.bookproject1.book_feature.presentation.component.logic.BookPart

data class HomeUiState(
    val parts: List<BookPart> = emptyList(),
    val isGridLayout: Boolean = true, // true تعني عرض شبكي (Grid)، false تعني قائمة (List)
    val isLoading: Boolean = false
)