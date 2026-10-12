package com.example.bookproject1.book_feature.presentation.home.logic

sealed interface HomeAction {
    // تبديل العرض بين Grid و List
    data object OnToggleLayoutClick : HomeAction
}