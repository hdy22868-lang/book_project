package com.example.bookproject1.book_feature.presentation.component.logic

import com.example.bookproject1.R
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class BookPart(
    val partNumber: Int,
    @StringRes val titleRes: Int,
    @DrawableRes val coverRes: Int
){
    companion object {
        val defaultParts = listOf(
            BookPart(1, R.string.part_one, R.drawable.part1),
            BookPart(2, R.string.part_two, R.drawable.part2),
            BookPart(3, R.string.part_three, R.drawable.part3),
            BookPart(4, R.string.part_four, R.drawable.part4),
            BookPart(5, R.string.part_five, R.drawable.part5)
        )
    }
}