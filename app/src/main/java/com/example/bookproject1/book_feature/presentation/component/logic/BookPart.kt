package com.example.bookproject1.book_feature.presentation.component.logic

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class BookPart(
    val partNumber: Int,
    @StringRes val titleRes: Int,
    @DrawableRes val coverRes: Int,
    val isAvailable: Boolean = true
)