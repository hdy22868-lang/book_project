package com.example.bookproject1.book_feature.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SectionDto(
    @SerializedName("subheading") val subheading: String?,
    @SerializedName("paragraphs") val paragraphs: List<String>
)